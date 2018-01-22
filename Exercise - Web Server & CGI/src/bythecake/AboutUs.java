package bythecake;

public class AboutUs {
    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        sb.append("Content-Type: text/html").append(System.lineSeparator()).append(System.lineSeparator());

        sb.append("<html>" +
                HtmlElements.HEADER_ELEMENT +
                "<body>" +
                "<a href=\"/cgi-bin/menu.cgi\">Back to Menu</a>" +
                "<h2>About Us</h2>");


        sb.append("<dl>" +
                "<dt>ByTheCake EOOD</dt>" +
                "<dd>Name of the company</dd>" +
                "<dt>yvelikov</dt>" +
                "<dd>Owner</dd>" +
                "</dl>");

        sb.append("<pre style=\"background-color: #f94f80;\">" +
                "City: HongKong                " + "City: Salzburg" + System.lineSeparator() +
                "Address: ChoCoLad 18          " + "Address: SchokoLeiden 73" + System.lineSeparator() +
                "Phone: +78952804429           " + "Phone: +49241432990" + System.lineSeparator() +
                "</pre>");

        sb.append(HtmlElements.FOOTER_ELEMENT +
                "</body>" +
                "</html>");


        System.out.println(sb.toString());
        System.out.println();
    }
}
