package org.softuni.codexio.handlers;

import org.softuni.javache.http.*;
import org.softuni.javache.lib.handler.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaticRequestHandler implements RequestHandler {

    private static final String STATIC_FOLDER = "static";
    private static final String ERROR_404_MESSAGE = "The page or resource you are looking for is invalid.";

    @Override
    public byte[] handleRequest(String requestContent, String serverRootPath) {
        HttpRequest request = new HttpRequestImpl(requestContent);
        HttpResponse response = new HttpResponseImpl();

        String staticFolder = serverRootPath + File.separator + STATIC_FOLDER + File.separator;

        try {
            String resourceUrl = request.getRequestUrl();
            String resourceExtension = resourceUrl.substring(resourceUrl.lastIndexOf(".") + 1);

            byte[] content = Files.readAllBytes(Paths.get(staticFolder + resourceUrl));

            response.setStatusCode(HttpStatus.OK);
            response.addHeader("Content-Type", this.getContentType(resourceExtension));
            response.addHeader("Content-Length", String.valueOf(content.length));
            response.addHeader("Content-Disposition", "inline");
            response.setContent(content);

            return response.getBytes();
        } catch (IOException e) {
            return null;
        }
    }

    private String getContentType(String resourceExtension) {
        switch (resourceExtension){
            case "html": return "text/html";
            case "css": return "text/css";
            case "png": return "image/png";
            case "jpeg":
            case "jpg": return "image/jpeg";
            default: return "text/plain";
        }
    }
}
