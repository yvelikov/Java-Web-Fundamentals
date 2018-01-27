package bythecake;

public class Menu {
    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        sb.append("Content-Type: text/html").append(System.lineSeparator()).append(System.lineSeparator());

        sb.append("<html>" +
                HtmlElements.HEADER_ELEMENT +
                "<body>" +
                "<h1>By the Cake</h1>" +
                "<h2>Enjoy our awesome cakes</h2>" +
                "<hr />");


        sb.append(
                    "<ul>" +
                            "<li>" +
                            "<a href=\"/cgi-bin/home.cgi\">Home</a>" +
                            "<ol>" +
                            "<li><a href=\"/cgi-bin/home.cgi#cakes\">Our cakes</a></li>" +
                            "<li><a href=\"/cgi-bin/home.cgi#stores\">Our stores</a></li>" +
                            "</ol>" +
                            "</li>" +
                            "<li><a href=\"/cgi-bin/add_cake.cgi\">Add Cake</a></li>" +
                            "<li><a href=\"/cgi-bin/browse_cakes.cgi\">Browse cakes</a></li>" +
                            "<li><a href=\"/cgi-bin/about_us.cgi\">About us</a></li>" +
                            "</ul>");

        sb.append(
                    HtmlElements.FOOTER_ELEMENT + "</body>" +
                    "</html>");



        System.out.println(sb.toString());
        System.out.println();
    }
}
