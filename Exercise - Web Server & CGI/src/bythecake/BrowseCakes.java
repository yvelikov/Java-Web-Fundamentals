package bythecake;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrowseCakes {
    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        List<Cake> cakes = new ArrayList<>();

        sb.append("Content-Type: text/html").append(System.lineSeparator()).append(System.lineSeparator());

        sb.append("<html>" +
                HtmlElements.HEADER_ELEMENT +
                "<body>" +
                "<a href=\"/cgi-bin/menu.cgi\">Back to Menu</a>" +
                "<h2>Home</h2>");

        sb.append("<form method=\"get\" action=\"/cgi-bin/browse_cakes.cgi\">" +
                "<div>" +
                "<label for=\"cake_name\">Name: </label>" +
                "<input id=\"cake_name\" type=\"text\" name=\"cake_name\">" +
                "<input id=\"create\" type=\"submit\" name=\"search_button\" value=\"Search\">" +
                "</div>" +
                "</form>");


        Map<String, String> params = Request.createParameterMap(System.getenv("QUERY_STRING"));
        if (params.containsKey("cake_name")) {
            if (params.get("cake_name") != null) {
                String cakeType = params.get("cake_name").toLowerCase();

                BufferedReader cakesDatabase = null;
                try {
                    cakesDatabase = new BufferedReader(new FileReader("database.csv"));
                    cakesDatabase.lines().forEach(line -> {
                        String[] cakeData = line.split(",");
                        Cake cake = new Cake(cakeData[0], Double.valueOf(cakeData[1]));
                        cakes.add(cake);
                    });

                    cakes.stream().filter(cake -> cake.getName().toLowerCase().contains(cakeType)).forEach(cake -> {
                        sb.append(String.format("<p>Name: %s Price: %.2f</p>", cake.getName().replaceAll("\\+", " "), cake.getPrice()));
                    });
                } catch (FileNotFoundException e) {
                    sb.append("<h3>No cakes in the current Database</h3>");
                }
            }
        }

        sb.append(HtmlElements.FOOTER_ELEMENT +
                "</body>" +
                "</html>");

        System.out.println(sb.toString());
        System.out.println();
        System.out.println();
    }
}
