package com.automation.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response POJO representing a single user as returned by the JSONPlaceholder REST API.
 *
 * <p>Instances of this class are deserialised from the JSON body of responses to
 * {@code GET /users} and {@code GET /users/{id}}. Unknown JSON fields are silently
 * ignored via {@link JsonIgnoreProperties#ignoreUnknown()}.</p>
 *
 * <p>The class contains two nested static POJOs — {@link Address} and {@link Company} —
 * which map to the correspondingly named nested objects in the API response.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * UserResponse user = response.as(UserResponse.class);
 * Assert.assertNotNull(user.getEmail());
 * Assert.assertTrue(user.getEmail().contains("@"));
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    /** Server-assigned unique identifier of the user. */
    private Integer id;

    /** Full name of the user. */
    private String name;

    /** Login username of the user. */
    private String username;

    /** Email address of the user. */
    private String email;

    /** Phone number of the user. */
    private String phone;

    /** Personal website URL of the user. */
    private String website;

    /** Physical address of the user. */
    private Address address;

    /** Company the user works for. */
    private Company company;

    /**
     * Nested POJO representing a physical address.
     *
     * <p>Maps to the {@code address} object inside the JSONPlaceholder user response.</p>
     *
     * @author api-automation-framework
     * @version 1.0.0
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {

        /** Street name and number. */
        private String street;

        /** Apartment / suite identifier. */
        private String suite;

        /** City name. */
        private String city;

        /** Postal/zip code. */
        private String zipcode;
    }

    /**
     * Nested POJO representing the company a user works for.
     *
     * <p>Maps to the {@code company} object inside the JSONPlaceholder user response.</p>
     *
     * @author api-automation-framework
     * @version 1.0.0
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {

        /** Name of the company. */
        private String name;

        /** Company's catch-phrase / tagline. */
        private String catchPhrase;

        /** Company's business strategy statement. */
        private String bs;
    }
}
