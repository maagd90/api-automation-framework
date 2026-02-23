package com.automation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton manager that loads and exposes application configuration properties.
 *
 * <p>Properties are loaded in the following order, with later sources overriding earlier ones:
 * <ol>
 *   <li>{@code config/application.properties} from the test classpath</li>
 *   <li>{@code config/application-&lt;env&gt;.properties} where {@code env} is the value of the
 *       {@code -Denv} system property (defaults to {@code dev})</li>
 *   <li>All JVM system properties ({@code -D} flags)</li>
 * </ol>
 *
 * <p>Example usage:
 * <pre>{@code
 * ConfigurationManager config = ConfigurationManager.getInstance();
 * String baseUrl = config.getBaseUrl();
 * int timeout   = config.getConnectionTimeout();
 * }</pre>
 *
 * @see Environment
 */
public class ConfigurationManager {

    private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private final Properties properties = new Properties();
    private static final String DEFAULT_CONFIG = "config/application.properties";

    /** Private constructor – use {@link #getInstance()} instead. */
    private ConfigurationManager() {
        loadProperties(DEFAULT_CONFIG);
        overrideWithEnvironmentProperties();
    }

    /**
     * Returns the singleton {@code ConfigurationManager} instance, creating it if necessary.
     *
     * @return the singleton instance
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Loads properties from a classpath resource file into the internal {@link Properties} map.
     * If the file is not found, a warning is logged and the call is silently ignored.
     *
     * @param configFile the classpath-relative path to the properties file
     */
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

    /**
     * Loads environment-specific overrides and applies all JVM system properties on top.
     */
    private void overrideWithEnvironmentProperties() {
        String env = System.getProperty("env", "dev");
        String envConfig = "config/application-" + env + ".properties";
        loadProperties(envConfig);

        // Override with system properties
        System.getProperties().forEach((key, value) ->
                properties.setProperty(key.toString(), value.toString()));
    }

    /**
     * Returns the value of the named property, or {@code null} if it is not set.
     *
     * @param key the property key
     * @return the property value, or {@code null}
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property not found: {}", key);
        }
        return value;
    }

    /**
     * Returns the value of the named property, falling back to the given default when not set.
     *
     * @param key          the property key
     * @param defaultValue the value to return when the property is absent
     * @return the property value or {@code defaultValue}
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Returns the value of the named property parsed as an {@code int}.
     *
     * @param key          the property key
     * @param defaultValue the value to return when the property is absent or not a valid integer
     * @return the parsed integer value or {@code defaultValue}
     */
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

    /**
     * Returns the value of the named property parsed as a {@code boolean}.
     *
     * @param key          the property key
     * @param defaultValue the value to return when the property is absent
     * @return the parsed boolean value or {@code defaultValue}
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value.trim());
        }
        return defaultValue;
    }

    /**
     * Returns the REST API base URL (property key {@code api.base.url}).
     *
     * @return the base URL string, defaulting to {@code "https://jsonplaceholder.typicode.com"}
     */
    public String getBaseUrl() {
        return getProperty("api.base.url", "https://jsonplaceholder.typicode.com");
    }

    /**
     * Returns the GraphQL endpoint URL (property key {@code graphql.base.url}).
     *
     * @return the GraphQL URL string, defaulting to {@code "https://spacex-production.up.railway.app/"}
     */
    public String getGraphQLUrl() {
        return getProperty("graphql.base.url", "https://spacex-production.up.railway.app/");
    }

    /**
     * Returns the HTTP connection timeout in milliseconds (property key {@code api.connection.timeout}).
     *
     * @return the timeout in ms, defaulting to {@code 5000}
     */
    public int getConnectionTimeout() {
        return getIntProperty("api.connection.timeout", 5000);
    }

    /**
     * Returns the HTTP read timeout in milliseconds (property key {@code api.read.timeout}).
     *
     * @return the timeout in ms, defaulting to {@code 10000}
     */
    public int getReadTimeout() {
        return getIntProperty("api.read.timeout", 10000);
    }

    /**
     * Returns the maximum number of request retry attempts (property key {@code api.max.retries}).
     *
     * @return the retry count, defaulting to {@code 3}
     */
    public int getMaxRetries() {
        return getIntProperty("api.max.retries", 3);
    }
}
