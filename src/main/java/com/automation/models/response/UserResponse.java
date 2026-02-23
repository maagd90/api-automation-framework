package com.automation.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response model representing a User resource returned by the JSONPlaceholder API.
 *
 * <p>Includes nested {@link Address} and {@link Company} inner classes that mirror
 * the structure of the API response. Unmapped JSON fields are silently ignored.
 *
 * <p>Example usage:
 * <pre>{@code
 * Response response = client.getById(ApiConstants.USERS_ENDPOINT, 1);
 * UserResponse user = response.as(UserResponse.class);
 * System.out.println(user.getName() + " – " + user.getEmail());
 * }</pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    /** Server-assigned unique identifier for this user. */
    private Integer id;

    /** Full display name of the user. */
    private String name;

    /** Unique login username. */
    private String username;

    /** Contact e-mail address. */
    private String email;

    /** Physical or postal address of the user. */
    private Address address;

    /** Contact phone number. */
    private String phone;

    /** Personal website URL. */
    private String website;

    /** Employer company details. */
    private Company company;

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    /**
     * Represents the physical address associated with a user.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {

        /** Street name and house/apartment number. */
        private String street;

        /** Suite or apartment number. */
        private String suite;

        /** City name. */
        private String city;

        /** Postal / ZIP code. */
        private String zipcode;
    }

    /**
     * Represents the company associated with a user.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {

        /** Legal company name. */
        private String name;

        /** Company catch phrase / tagline. */
        private String catchPhrase;

        /** Business strategy string ({@code "bs"} field in the API). */
        private String bs;
    }
}
