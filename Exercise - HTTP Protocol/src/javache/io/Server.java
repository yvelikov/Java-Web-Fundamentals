package javache.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.FutureTask;

public class Server {

    private static final int SOCKET_TIMEOUT_MILLISECONDS = 5000;
    private static final String CLIENT_CONNECTED = "Client connected: ";
    private static final String TIMEOUT_DETECTED = "Timeout detected";
    private ServerSocket server;
    private int port;
    private long timeoutCount;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws IOException {
        this.server = new ServerSocket(this.port);
        this.server.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

        while (true){
            try(Socket clientSocket = this.server.accept();){
                clientSocket.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);
                System.out.println(CLIENT_CONNECTED + clientSocket.getPort());

                ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket, new RequestHandler());

                FutureTask<?> task = new FutureTask<>(connectionHandler, null );
                task.run();
            } catch (SocketTimeoutException ste){
                System.out.println(TIMEOUT_DETECTED);
                this.timeoutCount++;
            }
        }
    }
}
