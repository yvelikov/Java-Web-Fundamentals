package org.softuni.javache;

import org.softuni.javache.http.HttpSessionStorage;
import org.softuni.javache.http.HttpSessionStorageImpl;
import org.softuni.javache.util.RequestHandlerLoader;
import org.softuni.javache.util.ServerConfig;
import org.softuni.javache.util.StreamCachingService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.*;
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

    private RequestHandlerLoader requestHandlerLoader;

    private ServerConfig serverConfiguration;

    private WatchService watchService;

    public Server(int port) {
        this.port = port;
        this.timeouts = 0;
        this.serverConfiguration = new ServerConfig();
        this.requestHandlerLoader = new RequestHandlerLoader();
        this.loadThirdParties();

    }

    private void loadThirdParties() {
        this.initializeHandlersPriorityOrder();
//        this.startLoadingProcess();
        this.initializeRequestHandlers();
    }

    private void initializeWatchService() {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(WebConstants.WEB_SERVER_ROOT_FOLDER_PATH + "lib");
            path.register(this.watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            while ((key = watchService.take()) != null){
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    System.out.println("Event kind:" + watchEvent.kind() + ". File affected: " + watchEvent.context());
                    this.initializeRequestHandlers();
                    System.out.println("Handlers reloaded");
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private void startLoadingProcess() {
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//
//        executorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                initializeRequestHandlers();
//                initializeHandlersPriorityOrder();
//            }
//        },0,10, TimeUnit.SECONDS);
//
//    }

    private void initializeRequestHandlers() {
        this.requestHandlerLoader.loadRequestHandlers(this.serverConfiguration.getConfiguration().get("request-handlers"));
    }

    private void initializeHandlersPriorityOrder() {
        this.serverConfiguration.loadConfiguration();
    }

    public void run() throws IOException {
        this.server = new ServerSocket(this.port);
        System.out.println(LISTENING_MESSAGE + this.port);

        this.server.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

        HttpSessionStorage sessionStorage = new HttpSessionStorageImpl();

//        Thread watchServiceThread = new Thread(this::initializeWatchService);
//        watchServiceThread.start();

        while (true) {
            try (Socket clientSocket = this.server.accept()) {
                clientSocket.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

                ConnectionHandler connectionHandler = new ConnectionHandler(
                        clientSocket,
                        this.requestHandlerLoader.getLoadedRequestHandlers(),
                        new StreamCachingService()
                );

                FutureTask<?> task = new FutureTask<>(connectionHandler, null);
                task.run();
            } catch (SocketTimeoutException e) {
                System.out.println(TIMEOUT_DETECTION_MESSAGE);
                this.timeouts++;
            }
        }
    }
}