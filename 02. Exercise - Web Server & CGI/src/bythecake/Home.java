package bythecake;

public class Home {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("Content-Type: text/html").append(System.lineSeparator()).append(System.lineSeparator());

        sb.append("<html>" +
                        HtmlElements.HEADER_ELEMENT +
                        "<body>" +
                        "<a href=\"/cgi-bin/menu.cgi\">Back to Menu</a>" +
                        "<h2>Home</h2>");

        //Our Cakes
        sb.append(
                        "<section>" +
                        "<h3 id=\"cakes\">Our cakes</h3>" +
                        "<p><strong><em>Cake</em></strong> is a form of <strong><em>sweet</em></strong> <strong><em>dessert</em></strong> that is typically baked. " +
                        "In its oldest forms, <strong><em>cakes</em></strong> were modifications of breads, but " +
                        "<strong><em>cakes</em></strong> now cover a wide range of preparations that can be simple " +
                        "or elaborate, and that share features with other <strong><em>desserts</em></strong> such " +
                        "as pastries, meringues, custards, and pies.</p>" +
                        "<img src=\"/cake-image.jpg\" alt=\"Cake image\" width=\"512\" height=\"283\">" +
                        "</section>");

        //Our Stores
        sb.append(
                        "<section>" +
                        "<h3 id=\"stores\">Our stores</h3>" +
                        "<p>Our <strong><em>stores</em></strong> are located in 21 cities all over the world. Come and see what we have for you.</p>" +
                        "<img src=\"/cake-store-image.jpg\" alt=\"Store image\" width=\"512\" height=\"288\">" +
                        "</section>");

       sb.append( HtmlElements.FOOTER_ELEMENT +
                        "</body>" +
                        "</html>");



        System.out.println(sb.toString());
        System.out.println();
    }
}
