import java.io.IOException;
import java.net.URLDecoder;

public class Main {
    public static void main(String[] args) {

        try {
            String canonicalPath = URLDecoder.decode(Main.class.getResource("").getPath() + "casebook.jar","UTF-8");
            new JarUnzipUtil().unzipJar(canonicalPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
