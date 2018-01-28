package javache;

import db.Repository;
import db.entity.User;
import javache.http.*;
import javache.io.Reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RequestHandler {


    private HttpRequest httpRequest;

    private HttpResponse httpResponse;

    private ResponseBuilder responseBuilder;

    private HttpSession session;

    private Repository repository;

    public RequestHandler(HttpSession session, Repository repository) {
        this.session = session;
        this.repository = repository;
    }

    public byte[] handleRequest(String requestContent) {
        this.httpRequest = new HttpRequestImpl(requestContent);
        this.httpResponse = new HttpResponseImpl();
        this.responseBuilder = new ResponseBuilder(this.httpResponse);

        String requestUrl = this.httpRequest.getRequestUrl();

        switch (requestUrl) {
            case "/":
                return this.handleDefaultRouteRequest();
            case "/register":
                return this.handleRegisterRequest();
            case "/login":
                return this.handleLoginRequest();
            case "/users/profile":
                return this.handleProfileRequest();
            case "/logout":
                return this.handleLogoutRequest();
            case "/home":
                return this.handleHomeRequest();
            default:
                return this.handleResourceRequest(requestUrl);
        }
    }

    private byte[] handleDefaultRouteRequest() {
        try {
            String defaultUrl = "\\html\\index.html";
            byte[] fileByteData = Files.readAllBytes(Paths.get(WebConstants.PATH_ASSETS + defaultUrl));
            return this.responseBuilder.buildOkResponse(defaultUrl, fileByteData);
        } catch (IOException e) {
            return this.responseBuilder.buildNotFoundResponse(WebConstants.NOT_FOUND_RESPONSE.getBytes());
        }
    }

    private byte[] handleRegisterRequest() {
        String email = this.httpRequest.getBodyParameters().get("email");
        String password = this.httpRequest.getBodyParameters().get("password");
        String confirmPassword = this.httpRequest.getBodyParameters().get("confirmPassword");

        if (!password.equals(confirmPassword)) {
            return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
        }

        try {
            User existingUser = this.repository.findByEmail(email);
            if (existingUser != null) {
                return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
            }

            this.repository.persist(UUID.randomUUID().toString() + "|" + email + "|" + password);
            return this.responseBuilder.buildRedirectResponse("/html/login.html");

        } catch (IOException e) {
            return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
        }
    }

    private byte[] handleLoginRequest() {
        String email = this.httpRequest.getBodyParameters().get("email");
        String password = this.httpRequest.getBodyParameters().get("password");

        try {
            User user = this.repository.findByEmail(email);
            if (user == null) {
                return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
            }

            if (!user.getPassword().equals(password)) {
                return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
            }

            String sessionId = UUID.randomUUID().toString();

            this.session.setSessionData(sessionId, new HashMap<String, Object>() {{
                put("userId", user.getId());
            }});

            this.httpResponse.addCookie(WebConstants.JAVACHE_SESSION_ID, sessionId);
            return this.responseBuilder.buildRedirectResponse("/users/profile");

        } catch (IOException e) {
            return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
        }
    }

    private byte[] handleProfileRequest() {
        String requestUrl = "\\users\\profile.html";
        try {
            if (!this.httpRequest.getCookies().containsKey(WebConstants.JAVACHE_SESSION_ID)) {
                return this.responseBuilder.buildRedirectResponse("/html/login.html");

            } else if (this.session.hasSession(this.httpRequest.getCookies().get(WebConstants.JAVACHE_SESSION_ID))) {
                String sessionId = this.httpRequest.getCookies().get(WebConstants.JAVACHE_SESSION_ID);
                String userId = (String) this.session.getSessionData(sessionId).get("userId");
                User user = this.repository.findById(userId);
                if (user == null) {
                    return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
                }
                String profileContents = Reader.readAllLines(new FileInputStream(WebConstants.PATH_PAGES + requestUrl));
                String responseContent = String.format(profileContents, user.getName(), user.getPassword());
                return this.responseBuilder.buildOkResponse(requestUrl, responseContent.getBytes());
            }

            return this.responseBuilder.buildRedirectResponse("/html/login.html");
        } catch (IOException e) {
            return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
        }
    }

    private byte[] handleLogoutRequest() {
        String sessionId = this.httpRequest.getCookies().get(WebConstants.JAVACHE_SESSION_ID);
        this.httpResponse.addCookie("expires", String.valueOf(new Date()));
        this.httpResponse.addCookie("Max-Age", "-3600");
        this.session.removeSession(sessionId);
        return this.responseBuilder.buildRedirectResponse("/");
    }

    private byte[] handleHomeRequest() {
        String requestUrl = "\\home.html";
        try {
            if (!this.httpRequest.getCookies().containsKey(WebConstants.JAVACHE_SESSION_ID)) {
                return this.responseBuilder.buildRedirectResponse("/html/login.html");

            } else if (this.session.hasSession(this.httpRequest.getCookies().get(WebConstants.JAVACHE_SESSION_ID))) {
                String sessionId = this.httpRequest.getCookies().get(WebConstants.JAVACHE_SESSION_ID);
                String userId = (String) this.session.getSessionData(sessionId).get("userId");
                List<User> registeredUsers = this.repository.findAll();
                if (registeredUsers == null) {
                    return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
                }
                String profileContents = Reader.readAllLines(new FileInputStream(WebConstants.PATH_PAGES + requestUrl));
                StringBuilder users = new StringBuilder();
                for (User registeredUser : registeredUsers) {
                    if (!registeredUser.getId().equals(userId)) {
                        users.append(registeredUser.getName()).append("<br />");
                    }
                }
                String responseContent = String.format(profileContents, users.toString().trim());
                return this.responseBuilder.buildOkResponse(requestUrl, responseContent.getBytes());
            }

            return this.responseBuilder.buildRedirectResponse("/html/login.html");
        } catch (IOException e) {
            return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
        }
    }

    private byte[] handleResourceRequest(String requestUrl) {
        if (this.httpRequest.getCookies().containsKey(WebConstants.JAVACHE_SESSION_ID)) {
            if(this.session.hasSession(this.httpRequest.getCookies().get(WebConstants.JAVACHE_SESSION_ID))){
                if (requestUrl.equals("/html/register.html") || requestUrl.equals("/html/login.html"))
                    return this.responseBuilder.buildRedirectResponse("/users/profile");
            }
        }

        byte[] fileByteData = null;

        File file = new File(WebConstants.PATH_ASSETS + requestUrl);

        if (!file.exists() || file.isDirectory()) {
            return this.responseBuilder.buildNotFoundResponse(WebConstants.NOT_FOUND_RESPONSE.getBytes());
        }
        try {
            if (!file.getCanonicalPath().startsWith(WebConstants.PATH_ASSETS)) {
                return this.responseBuilder.buildBadRequestResponse(WebConstants.BAD_REQUEST_RESPONSE.getBytes());
            }

            fileByteData = Files.readAllBytes(Paths.get(WebConstants.PATH_ASSETS + requestUrl));
            return this.responseBuilder.buildOkResponse(requestUrl, fileByteData);

        } catch (IOException e) {
            return this.responseBuilder.buildNotFoundResponse(WebConstants.NOT_FOUND_RESPONSE.getBytes());
        }
    }
}
