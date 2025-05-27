package id.ac.ui.cs.advprog.papikos.chat.response;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testBuildWithAllFields() {
        String testData = "Test Data";
        long beforeTimestamp = System.currentTimeMillis();

        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(HttpStatus.OK)
                .message("Success Message")
                .data(testData)
                .build();

        long afterTimestamp = System.currentTimeMillis();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Success Message", response.getMessage());
        assertEquals(testData, response.getData());
        assertTrue(response.getTimestamp() >= beforeTimestamp && response.getTimestamp() <= afterTimestamp);
    }

    @Test
    void testBuildWithDefaultStatus() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .message("Default status test")
                .data("Some data")
                .build();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Default status test", response.getMessage());
        assertEquals("Some data", response.getData());
    }

    @Test
    void testBuildWithNullMessage() {
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .status(HttpStatus.ACCEPTED)
                .data(123)
                .build();

        assertEquals(HttpStatus.ACCEPTED.value(), response.getStatus());
        assertNull(response.getMessage());
        assertEquals(123, response.getData());
    }

    @Test
    void testBuildWithNullData() {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT)
                .message("No data here")
                .build();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals("No data here", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testOkConvenienceMethod() {
        Map<String, String> dataMap = Map.of("key", "value");
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .ok(dataMap);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Success", response.getMessage());
        assertEquals(dataMap, response.getData());
    }

    @Test
    void testCreatedConvenienceMethod() {
        String resourceId = "res-123";
        ApiResponse<String> response = ApiResponse.<String>builder()
                .created(resourceId);

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals("Resource created successfully", response.getMessage());
        assertEquals(resourceId, response.getData());
    }

    @Test
    void testBadRequestConvenienceMethod() {
        ApiResponse<Object> response = ApiResponse.builder()
                .badRequest("Invalid input parameters");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid input parameters", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testNotFoundConvenienceMethod() {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .notFound("Resource with ID 999 not found");

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("Resource with ID 999 not found", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testInternalErrorConvenienceMethod() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .internalError("An unexpected error occurred");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals("An unexpected error occurred", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testTimestampIsGenerated() {
        ApiResponse<String> response = ApiResponse.<String>builder().ok("Data");
        assertTrue(response.getTimestamp() > 0, "Timestamp should be a positive value");

        long currentTime = System.currentTimeMillis();
        assertTrue(Math.abs(currentTime - response.getTimestamp()) < 1000,
                "Timestamp should be close to the current time");
    }

    @Test
    void testBuilderReturnsSameInstanceForChaining() {
        ApiResponse.Builder<String> builder = ApiResponse.builder();
        assertSame(builder, builder.status(HttpStatus.OK));
        assertSame(builder, builder.message("Test"));
        assertSame(builder, builder.data("Data"));
    }

    @Test
    void testStaticBuilderMethod() {
        assertNotNull(ApiResponse.builder(), "Static builder method should return a Builder instance.");
        assertTrue(ApiResponse.builder() instanceof ApiResponse.Builder, "Static builder method should return an instance of ApiResponse.Builder.");
    }
}