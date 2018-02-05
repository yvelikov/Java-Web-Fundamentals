package org.softuni.javache.lib.handler;

public abstract class AbstractRequestHandler implements RequestHandler{

    protected final String serverPath;

    protected AbstractRequestHandler(String serverPath) {
        this.serverPath = serverPath;
    }
}
