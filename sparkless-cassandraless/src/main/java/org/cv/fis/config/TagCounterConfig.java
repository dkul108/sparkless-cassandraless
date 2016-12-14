package org.cv.fis.config;


import com.google.common.base.Charsets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public interface TagCounterConfig {

    int NUMBER_OF_THREADS = 16;

    String NEW_LINE_CHAR_SEPARATOR = "\n";

    interface ParseStrategy {

        String DEFAULT_UTF_8_CHARSET = Charsets.UTF_8.displayName();

        int MIN_WORD_LENGTH = 5;

        String DELIMETER = " ";

        String EMPTY_STRING = "";

        boolean TO_LOWER_CASE = true;

        String EXCLUDED_CHARS = "[<![CDATA[¢`~!@#$%^&*()_+-=[\\]\\\\;\',.¬/¦{}|:\"“”<>?\\r\\t\\n\\f]]]>]";

        Pattern EXCLUDED_CHARS_PATTERN = Pattern.compile(EXCLUDED_CHARS);
    }

    char CMD_KEY_START_SIGN = '-';

    String DIRECTORY_PATH_CMD_KEY = "d";

    String SOCKET_PORT_CMD_KEY = "p";

    int DEFAULT_SOCKET_PORT_NUMBER = 5432;

    String HOSTNAME = getHostname();

    static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "localhost";
        }
    }

    String EXIT_COMMAND = "quit";

}
