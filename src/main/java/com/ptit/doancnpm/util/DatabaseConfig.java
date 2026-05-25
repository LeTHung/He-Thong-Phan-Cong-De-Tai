package com.ptit.doancnpm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record DatabaseConfig(String url, String username, String password) {
    private static final String CONFIG_FILE = "/config/db.properties";

    public static DatabaseConfig load() {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing " + CONFIG_FILE);
            }
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read database config", exception);
        }

        String url = readValue("DB_URL", properties, "db.url");
        String username = readValue("DB_USERNAME", properties, "db.username");
        String password = readValue("DB_PASSWORD", properties, "db.password");
        return new DatabaseConfig(url, username, password);
    }

    private static String readValue(String envName, Properties properties, String propertyName) {
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return properties.getProperty(propertyName, "").trim();
    }
}
