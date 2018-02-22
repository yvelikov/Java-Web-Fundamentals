package org.softuni.broccolina.solet;

import org.softuni.javache.http.HttpStatus;

public abstract class BaseHttpSolet implements HttpSolet {

    private static final String NOT_FOUND_MESSAGE = "<h1>ERROR 404: %s Not Found</h1>";

    private boolean isInitialized;

    private SoletConfig soletConfig;

    protected BaseHttpSolet(SoletConfig soletConfig) {
        this.soletConfig = soletConfig;
    }

    @Override
    public void init() {
        this.isInitialized = true;
    }

    @Override
    public boolean isInitialized() {
        return this.isInitialized;
    }

    @Override
    public SoletConfig getSoletConfig() {
        return this.soletConfig;
    }

    @Override
    public void service(HttpSoletRequest request, HttpSoletResponse response) {
        switch (request.getMethod()) {
            case "GET":
                this.doGet(request, response);
                break;
            case "POST":
                this.doPost(request, response);
                break;
            case "PUT":
                this.doPut(request, response);
                break;
            case "DELETE":
                this.doDelete(request, response);
                break;
        }
    }

    @Override
    public void doGet(HttpSoletRequest request, HttpSoletResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.addHeader("Content-Type", "text/html");
        response.setContent(String.format(NOT_FOUND_MESSAGE, "GET action").getBytes());
    }

    @Override
    public void doPost(HttpSoletRequest request, HttpSoletResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.addHeader("Content-Type", "text/html");
        response.setContent(String.format(NOT_FOUND_MESSAGE, "POST action").getBytes());
    }

    @Override
    public void doPut(HttpSoletRequest request, HttpSoletResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.addHeader("Content-Type", "text/html");
        response.setContent(String.format(NOT_FOUND_MESSAGE, "PUT action").getBytes());
    }

    @Override
    public void doDelete(HttpSoletRequest request, HttpSoletResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.addHeader("Content-Type", "text/html");
        response.setContent(String.format(NOT_FOUND_MESSAGE, "DELETE action").getBytes());
    }
}
