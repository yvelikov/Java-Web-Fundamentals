package javache.http;

public enum HttpContentType {
    HTML("text/html"),
    CSS("text/css"),
    PNG("image/png"),
    JPG("image/jpeg"),
    JPEG("image/jpeg");

    private final String type;

    HttpContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
