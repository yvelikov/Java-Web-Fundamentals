package org.softuni.javache.util;

import org.softuni.javache.Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class ServerConfig {

    private static final String CONFIGURATION_PATH = Server.class.getResource("").getPath().replace("org/softuni/javache/", "config.ini");

    private Map<String, LinkedHashSet<String>> configuration;

    public ServerConfig() {
        this.configuration = new HashMap<>();
    }

    public void loadConfiguration() {

        String path = CONFIGURATION_PATH.replaceAll("%20", " ");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            while (reader.ready()) {
                String[] currentConfig = reader.readLine().split(": ");
                String resourceType = currentConfig[0];
                String[] resources = currentConfig[1].split(", ");
                this.configuration.putIfAbsent(resourceType, new LinkedHashSet<>());

                for (String resource : resources) {
                    this.configuration.get(resourceType).add(resource);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, LinkedHashSet<String>> getConfiguration() {
        return Collections.unmodifiableMap(this.configuration);
    }
}
