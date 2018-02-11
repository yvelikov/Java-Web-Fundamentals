package org.softuni.javache.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Reader {
    private Reader() {}

    public static String readAllLines(InputStream inputStream) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder result = new StringBuilder();


        appendInputStream(buffer, result);

        return result.toString();
    }

    private static void appendInputStream(BufferedReader buffer, StringBuilder result) throws IOException {
        while(buffer.ready()) {
            result.append((char)buffer.read());
        }
    }
}