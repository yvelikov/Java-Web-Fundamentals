package javache.io;

public final class WebConstants {

    public static final String GET_METHOD = "GET";

    public static final String POST_METHOD = "POST";

    public static final String HTTP_VERSION = "HTTP/1.1";

    public static final String STATUS_OK = "OK";

    public static final int STATUS_CODE_OK = 200;

    public static final String STATUS_CREATED = "Created";

    public static final int STATUS_CODE_CREATED = 201;

    public static final String STATUS_FOUND = "Found";

    public static final int STATUS_CODE_FOUND = 302;

    public static final String STATUS_BAD_REQUEST = "Bad Request";

    public static final int STATUS_CODE_BAD_REQUEST = 400;

    public static final String STATUS_UNAUTHORIZED = "Unauthorized";

    public static final int STATUS_CODE_UNAUTHORIZED = 401;

    public static final String STATUS_NOT_FOUND = "Not Found";

    public static final int STATUS_CODE_NOT_FOUND = 404;

    public static final String STATUS_INTERNAL_SERVER_ERROR = "Internal Server Error";

    public static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;

    public static final String SERVER_HEADER = "Server";

    public static final String SERVER_NAME_AND_VERSION = "Javache/-0.0.5";

    public static final String DATE_HEADER = "Date";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final String CONTENT_TYPE_HTML = "text/html";

    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

    public static final String CONTENT_LENGTH_HEADER = "Content-Length";

    public static final String CONTENT_DISPOSITION_VALUE_INLINE = "inline";

    public static final String PATH_ASSETS = "C:\\Users\\345\\Documents\\Java Web Fundamentals Git\\Java-Web-Fundamentals\\Exercise - HTTP Protocol\\src\\resources\\assets";

    public static final String PATH_PAGES = "C:\\Users\\345\\Documents\\Java Web Fundamentals Git\\Java-Web-Fundamentals\\Exercise - HTTP Protocol\\src\\resources\\pages";

    public static final String NOT_FOUND_RESPONSE = "<h1>404 Not Found</h1><p>The requested URL %s was not found on this server.</p>";

    public static final String ACCESS_DENIED_RESPONSE = "<h1>401 Authorization Required</h1><p>This server could not verify that you are authorized to access the document requested.</p>";

    public static final String INTERNAL_SERVER_ERROR_RESPONSE = "<h1>500 Internal Server Error</h1><p>The server encountered an internal error or misconfiguration and was unable to complete your request.</p>";

    private WebConstants() {
    }
}
