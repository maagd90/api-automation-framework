package com.automation.config;

/**
 * Enumeration of the supported deployment environments for the automation framework.
 *
 * <p>Each constant carries a lower-case string label that matches the {@code env}
 * JVM system property used by {@link ConfigurationManager} to select the correct
 * environment-specific properties file (e.g. {@code application-dev.properties}).</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Environment env = Environment.fromString(System.getProperty("env", "dev"));
 * System.out.println(env.getValue()); // "dev"
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see ConfigurationManager
 */
public enum Environment {

    /** Local development environment. */
    DEV("dev"),

    /** Pre-production / staging environment. */
    STAGING("staging"),

    /** Production environment. */
    PROD("prod");

    /** Lower-case string label used in property-file names and system properties. */
    private final String value;

    /**
     * Constructs an {@code Environment} constant with the given string label.
     *
     * @param value the lower-case environment label (e.g. {@code "dev"})
     */
    Environment(String value) {
        this.value = value;
    }

    /**
     * Returns the lower-case string label of this environment.
     *
     * @return the environment label (e.g. {@code "dev"}, {@code "staging"}, {@code "prod"})
     */
    public String getValue() {
        return value;
    }

    /**
     * Resolves an {@code Environment} constant from a string label, ignoring case.
     *
     * <p>If no constant matches {@code env}, {@link #DEV} is returned as the safe
     * default.</p>
     *
     * @param env the string label to look up (may be {@code null}; treated as no-match)
     * @return the matching {@code Environment} constant, or {@link #DEV} if not found
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
