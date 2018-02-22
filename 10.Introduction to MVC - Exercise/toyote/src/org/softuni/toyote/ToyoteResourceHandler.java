package org.softuni.toyote;

import org.softuni.javache.RequestHandler;
import org.softuni.javache.http.*;
import org.softuni.javache.io.Reader;
import org.softuni.javache.io.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ToyoteResourceHandler implements RequestHandler {

    private static final String STATIC = "static";
    private static final String NOT_FOUND = "ERROR 404. Resource or page that you are looking for cannot be found.";

    private String serverRootPath;

    private boolean isIntercepted;

    public ToyoteResourceHandler(String serverRootPath) {
        this.serverRootPath = serverRootPath.replaceAll("%20", " ");

    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {

        HttpRequest request = null;
        HttpResponse response = new HttpResponseImpl();

        try {
            String requestContent = new Reader().readAllLines(inputStream);
            request = new HttpRequestImpl(requestContent);
            String staticFolderRootPath = this.serverRootPath + STATIC;
            String requestUrl = request.getRequestUrl();
            String resourceExtension = requestUrl.substring(requestUrl.lastIndexOf(".") + 1);
            String fullResourcePath = (staticFolderRootPath + requestUrl);

            byte[] content = Files.readAllBytes(Paths.get(fullResourcePath));

            response.setStatusCode(HttpStatus.OK);
            response.addHeader("Content-Type", this.getContentType(resourceExtension));
            response.addHeader("Content-Length", String.valueOf(content.length));
            response.addHeader("Content-Disposition", "inline");

            response.setContent(content);
            new Writer().writeBytes(response.getBytes(),outputStream);
            this.isIntercepted = true;

        } catch (IOException e) {
//            response.setStatusCode(HttpStatus.NOT_FOUND);
//            response.setContent(NOT_FOUND.getBytes());
//            try {
//                Writer.writeBytes(response.getBytes(),outputStream);
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
            this.isIntercepted = false;
        }
    }

    private String getContentType(String resourceExtension) {
        switch (resourceExtension) {
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            default:
                return "text/plain";
        }
    }

    @Override
    public boolean hasIntercepted() {
        return this.isIntercepted;
    }
}
