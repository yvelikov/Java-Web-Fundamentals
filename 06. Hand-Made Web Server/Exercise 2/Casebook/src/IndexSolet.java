import org.softuni.broccolina.solet.BaseHttpSolet;
import org.softuni.broccolina.solet.HttpSoletRequest;
import org.softuni.broccolina.solet.HttpSoletResponse;
import org.softuni.broccolina.solet.WebSolet;
import org.softuni.javache.http.HttpStatus;

@WebSolet(route = "/index")
public class IndexSolet extends BaseHttpSolet {

    @Override
    public void doGet(HttpSoletRequest request, HttpSoletResponse response) {
        response.setStatusCode(HttpStatus.OK);
        response.addHeader("Content-Type", "text/html");
        response.setContent("<h1>Welcome to Casebook</h1>".getBytes());

//        try {
//            Writer.writeBytes(response.getBytes(),response.getResponseStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
