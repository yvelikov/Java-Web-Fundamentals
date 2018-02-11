package org.softuni.javache;

import org.softuni.javache.http.HttpSessionStorageImpl;
import org.softuni.javache.http.HttpSessionStorage;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final String LISTENING_MESSAGE = "Listening on port: ";

    private static final String TIMEOUT_DETECTION_MESSAGE = "Timeout detected!";

    private static final Integer SOCKET_TIMEOUT_MILLISECONDS = 5000;

    private int port;

    private int timeouts;

    private ServerSocket server;

    private Set<RequestHandler> requestHandlers;

    public Server(int port) {
        this.port = port;
        this.timeouts = 0;
        this.startLoadingProcess();
    }

    private void startLoadingProcess() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                initializeRequestHandlers();
                System.out.println("Handlers loaded");
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void initializeRequestHandlers() {
        this.requestHandlers = new RequestHandlerLoader().loadRequestHandlers();
    }

    public void run() throws IOException {
        this.server = new ServerSocket(this.port);
        System.out.println(LISTENING_MESSAGE + this.port);

        this.server.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

        HttpSessionStorage sessionStorage = new HttpSessionStorageImpl();


        while(true) {
            try(Socket clientSocket = this.server.accept()) {
                clientSocket.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

                ConnectionHandler connectionHandler
                        = new ConnectionHandler(clientSocket, this.requestHandlers);

                FutureTask<?> task = new FutureTask<>(connectionHandler, null);
                task.run();
            } catch(SocketTimeoutException e) {
                System.out.println(TIMEOUT_DETECTION_MESSAGE);
                this.timeouts++;
            }
        }
    }
}