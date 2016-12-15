package org.cv.fis;


import javafx.util.Pair;
import org.cv.fis.common.InputStreamProvider;
import org.cv.fis.files.provider.FileRepositoryConnection;
import org.cv.fis.files.provider.LocalFileConnection;
import org.cv.fis.network.TcServerSocket;
import org.cv.fis.parser.ParseResults;
import org.cv.fis.parser.Parser;
import org.cv.fis.parser.impl.TextParser;
import org.cv.fis.pool.TagCounterExecutor;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static org.cv.fis.config.TagCounterConfig.*;

public class TagCounter {

    public static void main(String... args) throws Exception {

        Map<String, List<String>> argsMap = parseArgs(args);
        int port = getPort(argsMap);
        String dirName = getDirectory(argsMap);
        System.out.println(String.format("Config: port=%d, dir=%s", port, dirName));


        ParseResults totals = ParseResults.inst();


        try (FileRepositoryConnection connection = LocalFileConnection.open(dirName)) {

            List<Pair<Parser, InputStreamProvider>> parseables =
                    connection.
                            listFiles().
                            stream().
                            map(fd -> new Pair<Parser, InputStreamProvider>(TextParser.inst(), fd)).
                            collect(Collectors.toList());

            CountDownLatch finishSignal = new CountDownLatch(parseables.size());

            TagCounterExecutor executor = TagCounterExecutor.completionAware(finishSignal);
            executor.execute(totals, parseables);

            finishSignal.await();
            System.out.println(totals.toString());
        }

        try (TcServerSocket serverSocket = TcServerSocket.of(port, totals)) {
            Console console = System.console();
            serverSocket.startHandleMultipleConnectons(console);
            System.exit(0);
        }
    }

    private static String getDirectory(Map<String, List<String>> argsMap) {
        return argsMap.get(DIRECTORY_PATH_CMD_KEY) == null ? "." : argsMap.get(DIRECTORY_PATH_CMD_KEY).get(0);
    }

    private static int getPort(Map<String, List<String>> argsMap) {
        return argsMap.get(SOCKET_PORT_CMD_KEY) == null ?
                DEFAULT_SOCKET_PORT_NUMBER : parseInt(argsMap.get(SOCKET_PORT_CMD_KEY).get(0));
    }

    private static Map<String, List<String>> parseArgs(String... args) {
        final Map<String, List<String>> params = new HashMap<>();
        List<String> options = null;
        for (int i = 0; i < args.length; i++) {
            final String a = args[i];
            if (a.charAt(0) == CMD_KEY_START_SIGN) {
                if (a.length() != 2) {
                    System.err.println("Error at argument " + a);
                    continue;
                }
                options = new ArrayList<>();
                params.put(a.substring(1), options);
            } else if (options != null) {
                options.add(a);
            } else {
                System.err.println("Illegal parameter usage");
            }
        }
        return params;
    }

}
