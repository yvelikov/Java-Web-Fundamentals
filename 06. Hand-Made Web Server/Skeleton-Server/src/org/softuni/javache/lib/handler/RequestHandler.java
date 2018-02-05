package org.softuni.javache.lib.handler;

public interface RequestHandler {

    byte[] handleRequest(String requestContent, String serverExecutingPath);
}
