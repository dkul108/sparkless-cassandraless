package org.cv.fis.network;


import com.google.common.base.Preconditions;
import org.cv.fis.parser.ParseResults;

import javax.net.ServerSocketFactory;
import java.io.Console;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.cv.fis.config.TagCounterConfig.EXIT_COMMAND;

public class TcServerSocket implements AutoCloseable {

    private int port;
    private ParseResults totals;
    private ServerSocket socket;

    private TcServerSocket(int port, ParseResults totals) {
        Preconditions.checkState(port > 0);
        Preconditions.checkNotNull(totals);
        this.totals = totals;
        this.port = port;
    }

    private void init() throws IOException {
        socket = ServerSocketFactory.getDefault().createServerSocket(port);
    }

    public static TcServerSocket of(int port, ParseResults totals) throws IOException {
        TcServerSocket wrapper = new TcServerSocket(port, totals);
        wrapper.init();
        return wrapper;
    }

    public void startHandleMultipleConnectons(Console console) throws IOException, InterruptedException {
        for (; ; ) {

            Socket clientSocket = socket.accept();
            System.out.println("clientSocket accepted=" + clientSocket.isConnected());
            new Thread(new TcConnectionHandler(clientSocket, totals)).start();

            String input = console == null ? null : console.readLine();
            if (EXIT_COMMAND.equals(input)) {
                break;
            }
        }
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}
