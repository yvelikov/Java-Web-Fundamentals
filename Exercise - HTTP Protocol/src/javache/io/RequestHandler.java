package javache.io;

import javache.http.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Date;

public class RequestHandler {

    public byte[] handleRequest(String requestContent) {
        HttpRequest httpRequest = new HttpRequestImpl(requestContent);
        HttpResponse httpResponse = new HttpResponseImpl();

        if (httpRequest.getMethod().equals(WebConstants.GET_METHOD)) {
            this.handleGetRequest(httpRequest, httpResponse);

        } else if (httpRequest.getMethod().equals(WebConstants.POST_METHOD)) {
            this.handlePostRequest(httpRequest, httpResponse);
        }

        return httpResponse.getBytes();
    }

    private void handleGetRequest(HttpRequest httpRequest, HttpResponse httpResponse) {

        byte[] fileByteData = null;
        HttpContentType contentType = null;
        int resourceSize = 0;

        String requestUrl = httpRequest.getRequestUrl();

        try {
            if (httpRequest.isResource()) {
                fileByteData = Files.readAllBytes(Paths.get(WebConstants.PATH_ASSETS + httpRequest.getRequestUrl()));
                contentType = HttpContentType.valueOf(requestUrl.substring(requestUrl.lastIndexOf(".") + 1).toUpperCase());
            } else {
                fileByteData = Files.readAllBytes(Paths.get(WebConstants.PATH_PAGES + httpRequest.getRequestUrl() + ".html"));
                contentType = HttpContentType.HTML;
            }
            resourceSize = fileByteData.length;
            httpResponse.setStatusCode(WebConstants.STATUS_CODE_OK);
            this.constructHeaders(httpResponse, contentType, resourceSize);
            httpResponse.setContent(fileByteData);

        } catch (NoSuchFileException nfe) {
            this.handleNotFoundException(httpRequest, httpResponse);
        } catch (AccessDeniedException ade) {
            this.handleAccessDeniedException(httpRequest, httpResponse);
        } catch (IOException ioe) {
            this.handleIOException(httpResponse);
            ioe.printStackTrace();
        }
    }

    private void handlePostRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        // TODO: 1/24/2018
    }

    private void constructHeaders(HttpResponse httpResponse, HttpContentType httpContentType, int resourceSize) {
        httpResponse.addHeader(WebConstants.SERVER_HEADER, WebConstants.SERVER_NAME_AND_VERSION);
        httpResponse.addHeader(WebConstants.DATE_HEADER, String.valueOf(new Date()));
        httpResponse.addHeader(WebConstants.CONTENT_TYPE_HEADER, httpContentType.getType());
        httpResponse.addHeader(WebConstants.CONTENT_LENGTH_HEADER, String.valueOf(resourceSize));
        httpResponse.addHeader(WebConstants.CONTENT_DISPOSITION_HEADER, WebConstants.CONTENT_DISPOSITION_VALUE_INLINE);
    }

    private void handleNotFoundException(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatusCode(WebConstants.STATUS_CODE_NOT_FOUND);
        StringBuilder sb = new StringBuilder().append(String.format(WebConstants.NOT_FOUND_RESPONSE, httpRequest.getRequestUrl()));
        httpResponse.setContent(sb.toString().getBytes());
    }

    private void handleAccessDeniedException(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatusCode(WebConstants.STATUS_CODE_UNAUTHORIZED);
        StringBuilder sb = new StringBuilder().append(WebConstants.ACCESS_DENIED_RESPONSE);
        httpResponse.setContent(sb.toString().getBytes());
    }

    private void handleIOException(HttpResponse httpResponse) {
        httpResponse.setStatusCode(WebConstants.STATUS_CODE_INTERNAL_SERVER_ERROR);
        StringBuilder sb = new StringBuilder().append(WebConstants.INTERNAL_SERVER_ERROR_RESPONSE);
        httpResponse.setContent(sb.toString().getBytes());
    }
}
