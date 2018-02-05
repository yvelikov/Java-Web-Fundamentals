package org.softuni.javache;

import org.softuni.javache.io.Reader;
import org.softuni.javache.io.Writer;
import org.softuni.javache.lib.handler.RequestHandler;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler extends Thread {
    private Socket clientSocket;

    private InputStream clientSocketInputStream;

    private OutputStream clientSocketOutputStream;

    private Iterable<RequestHandler> requestHandlers;

    private String serverRootPath;

    public ConnectionHandler(Socket clientSocket, String serverRootPath, Iterable<RequestHandler> requestHandlers) {
        this.initializeConnection(clientSocket);
        this.requestHandlers = requestHandlers;
        this.serverRootPath = serverRootPath;
    }

    private void initializeConnection(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            this.clientSocketInputStream = this.clientSocket.getInputStream();
            this.clientSocketOutputStream = this.clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String requestContent = null;

            int i = 0;

            while (i++ < 5000) {
                requestContent = Reader.readAllLines(this.clientSocketInputStream);

                if (requestContent.length() > 0) {
                    break;
                }
            }

            byte[] responseContent = null;
            for (RequestHandler requestHandler : this.requestHandlers) {
                responseContent = requestHandler.handleRequest(requestContent, this.serverRootPath);
                if(responseContent!= null){
                    break;
                }
            }

            Writer.writeBytes(responseContent, this.clientSocketOutputStream);

            this.clientSocketInputStream.close();
            this.clientSocketOutputStream.close();
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






