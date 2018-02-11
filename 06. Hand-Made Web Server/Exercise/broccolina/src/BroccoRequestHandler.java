
import org.softuni.javache.RequestHandler;
import org.softuni.javache.http.HttpResponse;
import org.softuni.javache.http.HttpResponseImpl;
import org.softuni.javache.http.HttpStatus;
import org.softuni.javache.io.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BroccoRequestHandler implements RequestHandler {

    private boolean isIntercepted;

    private final String serverRoothPath;

    public BroccoRequestHandler(String serverRootPath) {
        this.isIntercepted = false;
        serverRoothPath = serverRootPath;
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {
        try {
            HttpResponse response = new HttpResponseImpl();
            response.setStatusCode(HttpStatus.OK);
            response.addHeader("Contnent-Type", "text/html");
            response.setContent(("<h1>Hello</h1><h2>"+ this.serverRoothPath + "</h2>").getBytes());

            Writer.writeBytes(response.getBytes(),outputStream);
            this.isIntercepted = true;
        } catch (IOException e) {
            this.isIntercepted = false;
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasIntercepted() {
        return this.isIntercepted;
    }
}
