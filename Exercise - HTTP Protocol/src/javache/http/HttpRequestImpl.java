package javache.http;

import javache.io.WebConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestImpl implements HttpRequest {

    private String method;
    private String requestUrl;
    private Map<String, String> headers;
    private Map<String, String> bodyParameters;

    public HttpRequestImpl(String requestContent) {
        this.headers = new HashMap<>();
        this.bodyParameters = new HashMap<>();
        this.parseRequestContent(requestContent);
    }

    private void parseRequestContent(String requestContent) {

//        if(requestContent == null || requestContent.length() == 0){
//
//        }

        String[] request = requestContent.split("\r\n");
        String[] requestLine = request[0].split("\\s");

        this.setMethod(requestLine[0]);
        this.setRequestUrl(requestLine[1]);

        if (this.getMethod().equals(WebConstants.GET_METHOD)) {
            this.seedHeaders(Arrays.copyOfRange(request, 1, request.length));
        } else if (this.getMethod().equals(WebConstants.POST_METHOD)) {
            this.seedHeaders(Arrays.copyOfRange(request, 1, request.length - 2));
            this.seedBodyParams(request[request.length - 1]);
        }
    }

    private void seedHeaders(String[] requestHeaders) {
        for (int i = 1; i < requestHeaders.length; i++) {
            String[] headerLine = requestHeaders[i].split(": ");
            String header = headerLine[0];
            String value = headerLine[1];
            this.addHeader(header, value);
        }
    }

    private void seedBodyParams(String bodyMessage) {
        String[] requestBodyParams = bodyMessage.split("&");
        for (String bodyPair : requestBodyParams) {
            String[] pairParams = bodyPair.split("=");
            String parameter = pairParams[0];
            String value = pairParams[1];
            this.addBodyParameter(parameter, value);
        }
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public Map<String, String> getBodyParameters() {
        return this.bodyParameters;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getRequestUrl() {
        return this.requestUrl;
    }

    @Override
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public void addHeader(String header, String value) {
        this.headers.putIfAbsent(header, value);
    }

    @Override
    public void addBodyParameter(String parameter, String value) {
        this.bodyParameters.putIfAbsent(parameter, value);
    }

    @Override
    public boolean isResource() {
        return this.getRequestUrl().contains(".");
    }
}
