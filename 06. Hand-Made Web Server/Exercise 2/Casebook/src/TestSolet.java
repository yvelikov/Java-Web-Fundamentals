import org.softuni.broccolina.solet.BaseHttpSolet;
import org.softuni.broccolina.solet.HttpSoletRequest;
import org.softuni.broccolina.solet.HttpSoletResponse;
import org.softuni.broccolina.solet.WebSolet;
import org.softuni.javache.http.HttpStatus;

@WebSolet(route = "/test")
public class TestSolet extends BaseHttpSolet {

    @Override
    public void doPost(HttpSoletRequest request, HttpSoletResponse response) {
        response.setStatusCode(HttpStatus.OK);
        response.addHeader("Content-Type", "text/html");
        response.setContent("<h1>Test</h1>".getBytes());

//        try {
//            Writer.writeBytes(response.getBytes(),response.getResponseStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
