package javache.io.utils;

import java.io.*;

public final class Reader {

    private Reader() {

    }

    public static String readAllLines(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder result = new StringBuilder();

        while (true) {
            if (!bufferedReader.ready()) {
                break;
            }

            result.append((char) bufferedReader.read());
        }
        return result.toString();
    }

}
