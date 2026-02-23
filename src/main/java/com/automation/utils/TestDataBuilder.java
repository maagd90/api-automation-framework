package com.automation.utils;

import com.automation.models.request.PostRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestDataBuilder {

    private static final Logger logger = LogManager.getLogger(TestDataBuilder.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();

    private TestDataBuilder() {}

    public static PostRequest buildPostRequest(String title, String body, int userId) {
        return PostRequest.builder()
                .title(title)
                .body(body)
                .userId(userId)
                .build();
    }

    public static PostRequest buildRandomPostRequest() {
        int userId = random.nextInt(10) + 1;
        return PostRequest.builder()
                .title("Test Post " + System.currentTimeMillis())
                .body("This is a test post body created at " + System.currentTimeMillis())
                .userId(userId)
                .build();
    }

    public static PostRequest buildPostFromTestData(String title, String body, int userId) {
        return PostRequest.builder()
                .title(title)
                .body(body)
                .userId(userId)
                .build();
    }

    public static List<Map<String, Object>> loadTestDataFromJson(String resourcePath) {
        try (InputStream is = TestDataBuilder.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                logger.warn("Test data file not found: {}", resourcePath);
                return List.of();
            }
            return objectMapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            logger.error("Error loading test data from {}: {}", resourcePath, e.getMessage());
            return List.of();
        }
    }

    public static Object[][] toTestNGDataProvider(List<Map<String, Object>> data) {
        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }
}
