package bythecake;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CreateCake {
    public static void main(String[] args) throws IOException {

        System.out.println("Content-Type: text/html");

        if (!System.getenv("REQUEST_METHOD").equals("POST")) {
            System.out.println();
            System.out.println("Cake creation requires POST method");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        Map<String, String> postParams = Request.createParameterMap(scanner.nextLine());

        if (!postParams.containsKey("create_button")) {
            System.out.println();
            System.out.println("A cake can be created only by using the Create button");
            return;
        }

        if(postParams.containsKey("cake_name") && postParams.containsKey("price")){
            Cake cake = new Cake(postParams.get("cake_name"),Double.valueOf(postParams.get("price")));

            FileWriter fw = new FileWriter("database.csv", true);
            fw.append(cake.toString()).append(System.lineSeparator());
            fw.flush();
            fw.close();

            System.out.println("Set-Cookie: cake_name=" + cake.getName() + "; ");
            System.out.println("Set-Cookie: price=" + cake.getPrice() + "; ");
        }

        System.out.println("Location: add_cake.cgi");
        System.out.println();
        System.out.println();

    }
}
