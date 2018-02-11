package org.softuni.broccolina;

import org.softuni.broccolina.solet.*;
import org.softuni.broccolina.util.SoletLoader;
import org.softuni.javache.RequestHandler;
import org.softuni.javache.http.HttpRequest;
import org.softuni.javache.http.HttpRequestImpl;
import org.softuni.javache.io.Reader;
import org.softuni.javache.io.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class SoletDispatcher implements RequestHandler {

    private final String SERVER_ROOT_PATH;

    private boolean isIntercepted;

    private String cachedInputStream;

    private SoletLoader soletLoader;

    public SoletDispatcher(String serverRootPath) {
        this.SERVER_ROOT_PATH = serverRootPath;
        this.isIntercepted = false;
        this.soletLoader = new SoletLoader(SERVER_ROOT_PATH);
        this.soletLoader.loadSolets();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {

        try {
            String requestContnent = Reader.readAllLines(inputStream);

            HttpRequest request = new HttpRequestImpl(requestContnent);
            HttpSoletResponse response = new HttpSoletResponseImpl(outputStream);

            for (Map.Entry<String, HttpSolet> soletEntry : this.soletLoader.getSolets().entrySet()) {

                String soletRoute = "/" + soletEntry.getKey();

                if (soletRoute.equals(request.getRequestUrl())) {

                    switch (request.getMethod()) {
                        case "GET":
                            soletEntry.getValue().doGet(
                                    new HttpSoletRequestImpl(requestContnent, null),
                                    response);

                            break;
                        case "POST":
                            soletEntry.getValue().doPost(
                                    new HttpSoletRequestImpl(requestContnent, null),
                                    response);

                            break;
                        case "PUT":
                            soletEntry.getValue().doPut(
                                    new HttpSoletRequestImpl(requestContnent, null),
                                    response);

                            break;
                        case "DELETE":
                            soletEntry.getValue().doDelete(
                                    new HttpSoletRequestImpl(requestContnent, null),
                                    response);

                            break;
                    }

                    Writer.writeBytes(response.getBytes(), outputStream);

                    this.isIntercepted = true;
                }
            }

        } catch (IOException e) {
            this.isIntercepted = false;
        }

    }

    @Override
    public boolean hasIntercepted() {
        return this.isIntercepted;
    }
}
