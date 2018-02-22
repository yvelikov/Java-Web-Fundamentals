package org.softuni.javache;

import org.softuni.javache.io.Reader;
import org.softuni.javache.util.ServerConfig;
import org.softuni.javache.util.StreamCachingService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class ConnectionHandler extends Thread {
    private Socket clientSocket;

    private InputStream clientSocketInputStream;

    private OutputStream clientSocketOutputStream;

    private StreamCachingService cachingService;

    private Map<String, RequestHandler> requestHandlers;

    public ConnectionHandler(Socket clientSocket, Map<String, RequestHandler> requestHandlers, StreamCachingService cachingService) {
        this.initializeConnection(clientSocket);
        this.requestHandlers = requestHandlers;
        this.cachingService = cachingService;
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

    private void processRequest() throws IOException {


        for (RequestHandler requestHandler : this.requestHandlers.values()) {
            try {
                requestHandler.handleRequest(this.cachingService.getOrCacheInputStream(this.clientSocketInputStream), this.clientSocketOutputStream);

            } catch (Exception e){
                ;
            }
            if (requestHandler.hasIntercepted()) {
                break;
            }
        }
    }

    @Override
    public void run() {
        try {

            this.processRequest();

            this.clientSocketInputStream.close();
            this.clientSocketOutputStream.close();
            this.clientSocket.close();
            this.cachingService.evictCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






