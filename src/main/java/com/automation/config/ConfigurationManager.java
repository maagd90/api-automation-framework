package com.automation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton configuration manager that loads and exposes framework-wide properties.
 *
 * <p>Properties are resolved from the following sources in ascending priority order
 * (later sources override earlier ones):</p>
 * <ol>
 *   <li>{@code src/test/resources/config/application.properties} – base defaults.</li>
 *   <li>{@code src/test/resources/config/application-{env}.properties} – environment-specific
 *       overrides, where {@code {env}} is the value of the JVM system property {@code env}
 *       (defaults to {@code "dev"} when absent).</li>
 *   <li>JVM system properties ({@code -Dkey=value}) – highest priority; always win.</li>
 * </ol>
 *
 * <p>The singleton is lazily initialised and thread-safe via {@code synchronized}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ConfigurationManager config = ConfigurationManager.getInstance();
 * String baseUrl  = config.getBaseUrl();
 * int    timeout  = config.getConnectionTimeout();
 * }</pre>
 *
 * @author automation-framework
 * @version 1.0.0
 * @see Environment
 */
public class ConfigurationManager {

    private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);

    /** The single shared instance of this class. */
    private static ConfigurationManager instance;

    /** Aggregated properties from all resolved configuration sources. */
    private final Properties properties = new Properties();

    /** Classpath location of the base properties file. */
    private static final String DEFAULT_CONFIG = "config/application.properties";

    /**
     * Private constructor – loads and merges all property sources.
     *
     * <p>Called once during singleton initialisation.</p>
     */
    private ConfigurationManager() {
        loadProperties(DEFAULT_CONFIG);
        overrideWithEnvironmentProperties();
    }

    /**
     * Returns the singleton {@code ConfigurationManager} instance.
     *
     * <p>The instance is created on the first call and reused for all subsequent calls.
     * This method is thread-safe.</p>
     *
     * @return the shared {@code ConfigurationManager} instance (never {@code null})
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Loads properties from the given classpath resource into {@link #properties}.
     *
     * <p>If the resource cannot be found, a warning is logged and the method
     * returns without modifying the existing property set. I/O errors are caught
     * and logged as errors.</p>
     *
     * @param configFile the classpath-relative path to the properties file
     *                   (e.g. {@code "config/application.properties"})
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
     * Loads environment-specific properties and applies JVM system-property overrides.
     *
     * <p>The active environment name is read from the {@code env} system property
     * (default: {@code "dev"}). The corresponding file
     * {@code config/application-{env}.properties} is loaded if it exists.</p>
     * <p>After that, every JVM system property is copied into the internal properties
     * map so that command-line overrides always take precedence.</p>
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
     * Returns the value of the named property, or {@code null} if it is not defined.
     *
     * <p>A warning is logged when the property is absent.</p>
     *
     * @param key the property name (must not be {@code null})
     * @return the property value, or {@code null} if no such property exists
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property not found: {}", key);
        }
        return value;
    }

    /**
     * Returns the value of the named property, falling back to a default when absent.
     *
     * @param key          the property name (must not be {@code null})
     * @param defaultValue the value to return when the property is not defined
     * @return the property value, or {@code defaultValue} if no such property exists
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Returns the value of the named property as an {@code int}.
     *
     * <p>If the property is absent, cannot be parsed as an integer, or is blank,
     * {@code defaultValue} is returned and a warning is logged.</p>
     *
     * @param key          the property name (must not be {@code null})
     * @param defaultValue the fallback integer value
     * @return the property value as an {@code int}, or {@code defaultValue}
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
     * Returns the value of the named property as a {@code boolean}.
     *
     * <p>Parsing follows {@link Boolean#parseBoolean(String)}: the string
     * {@code "true"} (case-insensitive) maps to {@code true}; all other non-null
     * values map to {@code false}. When the property is absent, {@code defaultValue}
     * is returned.</p>
     *
     * @param key          the property name (must not be {@code null})
     * @param defaultValue the fallback boolean value
     * @return the property value as a {@code boolean}, or {@code defaultValue}
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value.trim());
        }
        return defaultValue;
    }

    /**
     * Returns the REST API base URL.
     *
     * <p>Reads the {@code api.base.url} property; defaults to
     * {@code "https://jsonplaceholder.typicode.com"} when absent.</p>
     *
     * @return the REST API base URL (never {@code null})
     */
    public String getBaseUrl() {
        return getProperty("api.base.url", "https://jsonplaceholder.typicode.com");
    }

    /**
     * Returns the GraphQL endpoint URL.
     *
     * <p>Reads the {@code graphql.base.url} property; defaults to
     * {@code "https://spacex-production.up.railway.app/"} when absent.</p>
     *
     * @return the GraphQL endpoint URL (never {@code null})
     */
    public String getGraphQLUrl() {
        return getProperty("graphql.base.url", "https://spacex-production.up.railway.app/");
    }

    /**
     * Returns the HTTP connection timeout in milliseconds.
     *
     * <p>Reads the {@code api.connection.timeout} property; defaults to
     * {@code 5000} ms when absent or unparseable.</p>
     *
     * @return the connection timeout in milliseconds
     */
    public int getConnectionTimeout() {
        return getIntProperty("api.connection.timeout", 5000);
    }

    /**
     * Returns the HTTP read (socket) timeout in milliseconds.
     *
     * <p>Reads the {@code api.read.timeout} property; defaults to
     * {@code 10000} ms when absent or unparseable.</p>
     *
     * @return the read timeout in milliseconds
     */
    public int getReadTimeout() {
        return getIntProperty("api.read.timeout", 10000);
    }

    /**
     * Returns the maximum number of request retries on transient failures.
     *
     * <p>Reads the {@code api.max.retries} property; defaults to {@code 3} when
     * absent or unparseable.</p>
     *
     * @return the maximum retry count
     */
    public int getMaxRetries() {
        return getIntProperty("api.max.retries", 3);
    }
}
