package javache;

import javache.http.HttpContentType;
import javache.http.HttpResponse;
import javache.http.HttpStatus;

import java.util.Date;

public class ResponseBuilder {

    private HttpResponse httpResponse;

    public ResponseBuilder(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public byte[] buildOkResponse(String requestUrl, byte[] content){
        int resourceSize = content.length;
        HttpContentType contentType = HttpContentType.valueOf(requestUrl.substring(requestUrl.lastIndexOf(".") + 1).toUpperCase());
        this.constructHeaders(contentType, resourceSize);
        this.httpResponse.setStatusCode(HttpStatus.OK);
        this.httpResponse.setContent(content);
        return this.httpResponse.getBytes();
    }

    public byte[] buildNotFoundResponse(byte[] content) {
        this.httpResponse.setStatusCode(HttpStatus.NOT_FOUND);
        this.httpResponse.addHeader(WebConstants.CONTENT_TYPE_HEADER, WebConstants.CONTENT_TYPE_HTML);
        this.httpResponse.setContent(content);
        return this.httpResponse.getBytes();
    }

    public byte[] buildBadRequestResponse(byte[] content) {
        this.httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
        this.httpResponse.addHeader(WebConstants.CONTENT_TYPE_HEADER, WebConstants.CONTENT_TYPE_HTML);
        this.httpResponse.setContent(content);
        return this.httpResponse.getBytes();
    }

    public byte[] buildRedirectResponse(String location){
        this.httpResponse.setStatusCode(HttpStatus.SEE_OTHER);
        this.httpResponse.addHeader("Location", location);
        return this.httpResponse.getBytes();
    }

    private void constructHeaders(HttpContentType httpContentType, int resourceSize) {
        this.httpResponse.addHeader(WebConstants.SERVER_HEADER, WebConstants.SERVER_NAME_AND_VERSION);
        this.httpResponse.addHeader(WebConstants.DATE_HEADER, String.valueOf(new Date()));
        this.httpResponse.addHeader(WebConstants.CONTENT_TYPE_HEADER, httpContentType.getType());
        this.httpResponse.addHeader(WebConstants.CONTENT_LENGTH_HEADER, String.valueOf(resourceSize));
        this.httpResponse.addHeader(WebConstants.CONTENT_DISPOSITION_HEADER, WebConstants.CONTENT_DISPOSITION_VALUE_INLINE);
    }
}
