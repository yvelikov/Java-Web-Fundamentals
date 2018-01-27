package bythecake;

import java.util.Map;

public class AddCake {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        sb.append("Content-Type: text/html").append(System.lineSeparator()).append(System.lineSeparator());

        sb.append("<html>" +
                HtmlElements.HEADER_ELEMENT +
                "<body>" +
                "<a href=\"/cgi-bin/menu.cgi\">Back to Menu</a>" +
                "<h2>Home</h2>");

        sb.append("<form method=\"post\" action=\"/cgi-bin/create_cake.cgi\">" +
                "<div>" +
                "<label for=\"cake_name\">Name: </label>" +
                "<input id=\"cake_name\" type=\"text\" name=\"cake_name\">" +
                "<label for=\"price\"> Price: </label>" +
                "<input id=\"price\" type=\"number\" step=\"0.01\" name=\"price\">" +
                "<input id=\"create\" type=\"submit\" name=\"create_button\" value=\"Create\">" +
                "</div>" +
                "</form>");


        Map<String, String> cookies = Request.getCookieMap(System.getenv("HTTP_COOKIE"));
        if(cookies.containsKey("cake_name")){
            sb.append(
                    "<p>name: " + cookies.get("cake_name").replaceAll("\\+"," ") + "</p>" +
                            "<p>price: " + cookies.get("price") + "</p>");
        }

       sb.append(HtmlElements.FOOTER_ELEMENT +
                "</body>" +
                "</html>");

        System.out.println(sb.toString());
        System.out.println();
    }
}
