package org.softuni.javache;

import org.softuni.javache.io.Reader;
import org.softuni.javache.util.ServerConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class ConnectionHandler extends Thread {
    private Socket clientSocket;

    private String cachedInputStreamContent;

    private InputStream clientSocketInputStream;

    private OutputStream clientSocketOutputStream;

    private Map<String, RequestHandler> requestHandlers;

    private ServerConfig serverConfiguration;

    public ConnectionHandler(Socket clientSocket, Map<String, RequestHandler> requestHandlers, ServerConfig serverConfiguration) {
        this.initializeConnection(clientSocket);
        this.requestHandlers = requestHandlers;
        this.serverConfiguration = serverConfiguration;
    }

    public InputStream getClientSocketInputStream() throws IOException {
        if(this.cachedInputStreamContent == null){
            this.cachedInputStreamContent = Reader.readAllLines(this.clientSocketInputStream);
        }
        return new ByteArrayInputStream(this.cachedInputStreamContent.getBytes());
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


        for (String handlerName : this.serverConfiguration.getConfiguration().get("request-handlers")) {
            if(this.requestHandlers.containsKey(handlerName)){
                this.requestHandlers.get(handlerName).handleRequest(this.getClientSocketInputStream(), this.clientSocketOutputStream);
                if(this.requestHandlers.get(handlerName).hasIntercepted()){
                    break;
                }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






