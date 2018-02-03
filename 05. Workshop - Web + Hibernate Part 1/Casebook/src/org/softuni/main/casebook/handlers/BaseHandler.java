package org.softuni.main.casebook.handlers;

import org.softuni.main.javache.WebConstants;
import org.softuni.main.javache.http.*;

public abstract class BaseHandler {

    private static final String ERROR_400_MESSAGE = "Malformed request";
    private static final String ERROR_404_MESSAGE = "The page or resource you are looking for is invalid.";

    protected HttpSessionStorage sessionStorage;

    protected BaseHandler(HttpSessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
    }

    protected final boolean isLoggedIn(HttpRequest request) {
        if (!request.getCookies().containsKey(WebConstants.SERVER_SESSION_TOKEN)) {
            return false;
        }

        HttpCookie cookie = request.getCookies().get(WebConstants.SERVER_SESSION_TOKEN);

        if (this.sessionStorage.getSession(cookie.getValue()) == null) {
            return false;
        }

        return true;
    }

    protected final HttpResponse badRequest(HttpRequest request, HttpResponse response) {

        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.addHeader(WebConstants.CONTENT_TYPE_HEADER, WebConstants.CONTENT_TYPE_HTML);
        response.setContent(("<h1> Error (" + HttpStatus.BAD_REQUEST.getStatusCode() + "): " + ERROR_400_MESSAGE + "</h1>").getBytes());
        return response;
    }

    protected final HttpResponse notFound(HttpRequest request, HttpResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.addHeader(WebConstants.CONTENT_TYPE_HEADER, WebConstants.CONTENT_TYPE_HTML);
        response.setContent(("<h1> Error (" + HttpStatus.NOT_FOUND.getStatusCode() + "): " + ERROR_404_MESSAGE + "</h1>").getBytes());
        return response;
    }
}
