package org.cv.fis.network;


import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.cv.fis.parser.ParseResults;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class TcConnectionHandler implements Runnable {

    private Socket clientSocket;
    private ParseResults totals;

    public TcConnectionHandler(Socket clientSocket, ParseResults totals) {
        Preconditions.checkState(!clientSocket.isClosed());
        Preconditions.checkState(clientSocket.isBound());
        Preconditions.checkState(clientSocket.isConnected());

        this.clientSocket = clientSocket;
        this.totals = totals;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            boolean getSmthg = false;
            while (!getSmthg) {
                getSmthg = !Strings.isNullOrEmpty(totals.toString());
                if (getSmthg) {
                    out.println(totals.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}

