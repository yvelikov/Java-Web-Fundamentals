package org.softuni.web.extensions.handlers.lib;


import org.softuni.javache.http.HttpContext;
import org.softuni.javache.http.HttpSessionStorage;

public interface Application {
    void initializeRoutes();

    byte[] handleRequest(HttpContext httpContext);

    HttpSessionStorage getSessionStorage();

    void setSessionStorage(HttpSessionStorage sessionStorage);
}
