package com.automation.config;

/**
 * Enumeration of deployment environments supported by the framework.
 *
 * <p>The active environment is selected at runtime via the {@code -Denv} system property.
 * {@link ConfigurationManager} uses this enum to load the appropriate properties file.
 *
 * <p>Example usage:
 * <pre>{@code
 * Environment env = Environment.fromString(System.getProperty("env", "dev"));
 * System.out.println(env.getValue()); // "dev"
 * }</pre>
 *
 * @see ConfigurationManager
 */
public enum Environment {

    /** Local development environment. */
    DEV("dev"),

    /** Pre-production staging environment. */
    STAGING("staging"),

    /** Live production environment. */
    PROD("prod");

    /** The string value used in property file names and system properties. */
    private final String value;

    /**
     * Constructs an {@code Environment} with the given string identifier.
     *
     * @param value the lowercase environment identifier
     */
    Environment(String value) {
        this.value = value;
    }

    /**
     * Returns the lowercase string identifier for this environment.
     *
     * @return the environment value (e.g. {@code "dev"}, {@code "staging"}, {@code "prod"})
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the {@code Environment} whose value matches the given string,
     * ignoring case. Falls back to {@link #DEV} if no match is found.
     *
     * @param env the environment string to match
     * @return the matching {@code Environment}, or {@link #DEV} as default
     */
    public static Environment fromString(String env) {
        for (Environment e : values()) {
            if (e.value.equalsIgnoreCase(env)) {
                return e;
            }
        }
        return DEV;
    }
}
