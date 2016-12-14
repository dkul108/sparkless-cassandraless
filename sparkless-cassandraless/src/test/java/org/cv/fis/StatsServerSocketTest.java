package org.cv.fis;


import com.google.common.base.Preconditions;
import org.cv.fis.files.provider.SourceException;
import org.cv.fis.network.TcServerSocket;
import org.cv.fis.parser.ParseResults;
import org.fest.util.Strings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.net.SocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import static org.cv.fis.config.TagCounterConfig.HOSTNAME;
import static org.cv.fis.config.TagCounterConfig.ParseStrategy.EMPTY_STRING;
import static org.cv.fis.config.TagCounterConfig.DEFAULT_SOCKET_PORT_NUMBER;
import static org.fest.assertions.api.Assertions.assertThat;

public class StatsServerSocketTest {

    private ParsingFilesInParallelExecutorTest anotherTest;

    @Before
    public void setUp() throws IOException {
        anotherTest = new ParsingFilesInParallelExecutorTest();
        anotherTest.setUp();
    }

    @After
    public void tearDown() {
        anotherTest.tearDown();
    }

    /**
     * proper solution is to use CyclicBarrier thats waits all threads  initialized,
     * and/or lock.newCondition()/CountDownLatch to make things controllable and ordered
     */
    @Test
    public void testSocketsCommunication() throws Exception {
        ParseResults totals = ParseResults.inst();

        startThread(()-> {
            try {
                assertThat(TcClientSocket.of(HOSTNAME, DEFAULT_SOCKET_PORT_NUMBER).requestTotals()).isNotEmpty();
            } catch (IOException e) {
                failOnException(e);
            }
        });

        startThread(()-> {
            try {
                anotherTest.processLocalFilesParsingWithExecutor(anotherTest.getRootPath(), totals);
            } catch (SourceException e) {
                failOnException(e);
            }
        });

        startThread(()-> {
            try (TcServerSocket serverSocket = TcServerSocket.of(DEFAULT_SOCKET_PORT_NUMBER, totals)) {
                serverSocket.startHandleMultipleConnectons(null);
            } catch (Exception e) {
                failOnException(e);
            }
        });

        startThread(()-> {
            try {
                assertThat(TcClientSocket.of(HOSTNAME, DEFAULT_SOCKET_PORT_NUMBER).requestTotals()).isNotEmpty();
                assertThat(TcClientSocket.of(HOSTNAME, DEFAULT_SOCKET_PORT_NUMBER).requestTotals()).isNotEmpty();
            } catch (IOException e) {
                failOnException(e);
            }
        });

        Thread.sleep(1000);
        System.out.println("Koniec");
    }

    private void startThread(Runnable runnable) {
        new Thread(runnable::run).start();
    }

    private void failOnException(Exception e) {
        e.printStackTrace(System.err);
        Assert.fail(e.getMessage());
    }


    private static class TcClientSocket {

        private String hostname;
        private int port;
        private Socket clientSocket;

        private TcClientSocket(String hostname, int port) {
            Preconditions.checkState(port > 0);
            Preconditions.checkNotNull(hostname);
            this.hostname = hostname;
            this.port = port;

        }

        public static TcClientSocket of(String hostname, int port) throws IOException {
            TcClientSocket wrapper = new TcClientSocket(hostname, port);
            wrapper.init();
            return wrapper;
        }

        public void init() throws IOException {
            clientSocket = SocketFactory.getDefault().createSocket(hostname, port);
            Preconditions.checkState(!clientSocket.isClosed());
        }

        public String requestTotals() throws IOException {
            StringBuilder totalsSb = new StringBuilder(200);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String fromServer;
                while ((fromServer = in.readLine()) != null) {
                    if (!Strings.isNullOrEmpty(fromServer)) {
                        totalsSb.append(fromServer);
                    }
                }
            } catch (UnknownHostException e) {
                System.err.println("Unknown host " + hostname);
                return EMPTY_STRING;
            }
            System.out.println("Tags Count from server:" + totalsSb.toString());
            return totalsSb.toString();
        }
    }

}
