package javache.http;

import javache.io.WebConstants;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseImpl implements HttpResponse {

    private int statusCode;

    private Map<String, String> headers;

    private byte[] content;

    private Map<Integer, String> statusMessages;

    public HttpResponseImpl() {
        this.setContent(new byte[0]);
        this.headers = new LinkedHashMap<>();
        this.statusMessages = new LinkedHashMap<>();
        this.seedStatusMessages();
    }

    private void seedStatusMessages() {
        this.statusMessages.put(WebConstants.STATUS_CODE_OK, WebConstants.STATUS_OK);
        this.statusMessages.put(WebConstants.STATUS_CODE_CREATED, WebConstants.STATUS_CREATED);
        this.statusMessages.put(WebConstants.STATUS_CODE_FOUND, WebConstants.STATUS_FOUND);
        this.statusMessages.put(WebConstants.STATUS_CODE_BAD_REQUEST, WebConstants.STATUS_BAD_REQUEST);
        this.statusMessages.put(WebConstants.STATUS_CODE_UNAUTHORIZED, WebConstants.STATUS_UNAUTHORIZED);
        this.statusMessages.put(WebConstants.STATUS_CODE_NOT_FOUND, WebConstants.STATUS_NOT_FOUND);
        this.statusMessages.put(WebConstants.STATUS_CODE_INTERNAL_SERVER_ERROR, WebConstants.STATUS_INTERNAL_SERVER_ERROR);
    }

    private String getStatusMessage() {
        if (this.statusMessages.containsKey(this.statusCode)) {
            return this.statusMessages.get(this.statusCode);
        }
        return null;
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public byte[] getContent() {
        return this.content;
    }

    @Override
    public byte[] getBytes() {
        StringBuilder response = new StringBuilder().append(WebConstants.HTTP_VERSION).append(" ")
                .append(this.getStatusCode()).append(" ")
                .append(this.getStatusMessage()).append(System.lineSeparator());

        for (String header : this.headers.keySet()) {
            response.append(header).append(": ").append(this.headers.get(header)).append(System.lineSeparator());
        }

        response.append(System.lineSeparator());

        byte[] responseArray = response.toString().getBytes();

        byte[] httpResponse = new byte[responseArray.length + this.content.length];

//        for (int i = 0; i < responseArray.length; i++) {
//            httpResponse[i] = responseArray[i];
//        }
//
//        for (int i = 0; i < this.content.length; i++) {
//            httpResponse[i+responseArray.length] = this.content[i];
//        }

        System.arraycopy(responseArray, 0, httpResponse, 0, responseArray.length);

        System.arraycopy(this.content, 0, httpResponse, responseArray.length, this.content.length);

        return httpResponse;
    }

    @Override
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public void addHeader(String header, String value) {
        this.headers.putIfAbsent(header, value);
    }
}
