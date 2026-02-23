package com.automation.config;

public enum Environment {
    DEV("dev"),
    STAGING("staging"),
    PROD("prod");

    private final String value;

    Environment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Environment fromString(String env) {
        for (Environment e : values()) {
            if (e.value.equalsIgnoreCase(env)) {
                return e;
            }
        }
        return DEV;
    }
}
