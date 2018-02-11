package org.softuni.javache;

public final class WebConstants {
    public static final Integer DEFAULT_SERVER_PORT = 8000;

    public static final String WEB_SERVER_HTTP_VERSION = "HTTP/1.1";

    public static final String SERVER_SESSION_TOKEN = "Javache";

    public static final String WEB_SERVER_ROOT_FOLDER_PATH = Server.class
            .getResource("")
            .getPath()
            .replace("org/softuni/javache/", "")
            .replaceAll("%20", " ")
            .substring(1);

    private WebConstants() { }
}
