package org.softuni.javache;

import org.softuni.javache.http.*;

class RequestHandlerOld {
    private HttpContext httpContext;

//    private Application application;
//
//    RequestHandler(Application application) {
//        this.application = application;
//    }

    byte[] handleRequest(String requestContent) {
        return new byte[0];
//        HttpRequest httpRequest = new HttpRequestImpl(requestContent);
//        HttpResponse httpResponse = new HttpResponseImpl();
//
//        this.httpContext = new HttpContextImpl(httpRequest, httpResponse);
//
//        return this.application.handleRequest(this.httpContext);
    }
}
