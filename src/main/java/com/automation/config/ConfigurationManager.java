package com.automation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {

    private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private final Properties properties = new Properties();
    private static final String DEFAULT_CONFIG = "config/application.properties";

    private ConfigurationManager() {
        loadProperties(DEFAULT_CONFIG);
        overrideWithEnvironmentProperties();
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private void loadProperties(String configFile) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (is != null) {
                properties.load(is);
                logger.info("Loaded configuration from: {}", configFile);
            } else {
                logger.warn("Configuration file not found: {}", configFile);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration: {}", e.getMessage());
        }
    }

    private void overrideWithEnvironmentProperties() {
        String env = System.getProperty("env", "dev");
        String envConfig = "config/application-" + env + ".properties";
        loadProperties(envConfig);

        // Override with system properties, but only allow known automation-related prefixes
        // to prevent unintended configuration changes from arbitrary JVM properties.
        final String[] ALLOWED_PREFIXES = {"api.", "graphql.", "automation.", "env"};
        System.getProperties().forEach((key, value) -> {
            String keyStr = key.toString();
            for (String prefix : ALLOWED_PREFIXES) {
                if (keyStr.equals(prefix) || keyStr.startsWith(prefix)) {
                    properties.setProperty(keyStr, value.toString());
                    break;
                }
            }
        });
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property not found: {}", key);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer property '{}': {}", key, value);
            }
        }
        return defaultValue;
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value.trim());
        }
        return defaultValue;
    }

    public String getBaseUrl() {
        return getProperty("api.base.url", "https://jsonplaceholder.typicode.com");
    }

    public String getGraphQLUrl() {
        return getProperty("graphql.base.url", "https://spacex-production.up.railway.app/");
    }

    public int getConnectionTimeout() {
        return getIntProperty("api.connection.timeout", 5000);
    }

    public int getReadTimeout() {
        return getIntProperty("api.read.timeout", 10000);
    }

    public int getMaxRetries() {
        return getIntProperty("api.max.retries", 3);
    }
}
