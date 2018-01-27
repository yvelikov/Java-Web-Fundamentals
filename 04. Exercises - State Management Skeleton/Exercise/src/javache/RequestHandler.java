package javache;

import javache.http.*;
import db.entity.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RequestHandler {
    private HttpRequest httpRequest;

    private HttpResponse httpResponse;

    public RequestHandler(HttpSession session) {
        this.session = session;
    }

    public byte[] handleRequest(String requestContent) {
        
    }
}
