package org.softuni.applications.simpleapp;

import org.softuni.javache.http.HttpContext;
import org.softuni.javache.http.HttpSessionStorage;
import org.softuni.web.extensions.handlers.lib.Application;

public class MySimpleApp implements Application {

    private HttpSessionStorage sessionStorage;

    @Override
    public void initializeRoutes() {

    }

    @Override
    public byte[] handleRequest(HttpContext httpContext) {
        if(httpContext.getHttpRequest().getRequestUrl().contains("test-app")){
            return "HTTP/ 1.1 200 OK\r\nContent-type: text/html\r\n\r\n<h1>Dummy App invoked</h1>".getBytes();
        }
        return null;
    }

    @Override
    public HttpSessionStorage getSessionStorage() {
        return this.sessionStorage;
    }

    @Override
    public void setSessionStorage(HttpSessionStorage httpSessionStorage) {
        this.sessionStorage = sessionStorage;
    }
}
