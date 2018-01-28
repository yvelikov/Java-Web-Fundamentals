package javache;

public final class WebConstants {
    public static final Integer DEFAULT_SERVER_PORT = 8000;

    public static final String SERVER_HTTP_VERSION = "HTTP/1.1";

    public static final String JAVACHE_SESSION_ID = "JAVACHESSID";

    public static final String GET_METHOD = "GET";

    public static final String POST_METHOD = "POST";

    public static final String SERVER_HEADER = "Server";

    public static final String SERVER_NAME_AND_VERSION = "Javache/-0.0.5";

    public static final String DATE_HEADER = "Date";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final String CONTENT_TYPE_HTML = "text/html";

    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

    public static final String CONTENT_LENGTH_HEADER = "Content-Length";

    public static final String CONTENT_DISPOSITION_VALUE_INLINE = "inline";

    public static final String PATH_ASSETS = System.getProperty("user.dir") + "\\src\\resources\\assets";

    public static final String PATH_PAGES = System.getProperty("user.dir") + "\\src\\resources\\pages";

    public static final String NOT_FOUND_RESPONSE = "<h1>Error 404: Page or Resource not found...</h1>";

    public static final String BAD_REQUEST_RESPONSE = "<h1>Error 400: Malformed Request...</h1>";

    public static final String ACCESS_DENIED_RESPONSE = "<h1>401 Authorization Required</h1><p>This server could not verify that you are authorized to access the document requested.</p>";

    public static final String INTERNAL_SERVER_ERROR_RESPONSE = "<h1>500 Internal Server Error</h1><p>The server encountered an internal error or misconfiguration and was unable to complete your request.</p>";

    private WebConstants() { }
}
