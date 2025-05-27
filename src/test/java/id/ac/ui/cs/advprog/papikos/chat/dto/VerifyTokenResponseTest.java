package id.ac.ui.cs.advprog.papikos.chat.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerifyTokenResponseTest {

    @Test
    void testBuilderAndGetters() {
        VerifyTokenResponse.Data data = VerifyTokenResponse.Data.builder()
                .userId("user1")
                .email("user1@example.com")
                .role("ROLE_USER")
                .status("active")
                .build();

        VerifyTokenResponse response = VerifyTokenResponse.builder()
                .status(200)
                .message("Success")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();

        assertEquals(200, response.getStatus());
        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
        assertNotEquals(0, response.getTimestamp());

        assertEquals("user1", response.getData().getUserId());
        assertEquals("user1@example.com", response.getData().getEmail());
        assertEquals("ROLE_USER", response.getData().getRole());
        assertEquals("active", response.getData().getStatus());
    }

    @Test
    void testNoArgsAndSetters() {
        VerifyTokenResponse response = new VerifyTokenResponse();
        response.setStatus(404);
        response.setMessage("Not Found");
        response.setTimestamp(123456789L);

        VerifyTokenResponse.Data data = new VerifyTokenResponse.Data();
        data.setUserId("user2");
        data.setEmail("user2@example.com");
        data.setRole("ROLE_ADMIN");
        data.setStatus("inactive");

        response.setData(data);

        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getMessage());
        assertEquals(123456789L, response.getTimestamp());
        assertNotNull(response.getData());

        assertEquals("user2", response.getData().getUserId());
        assertEquals("user2@example.com", response.getData().getEmail());
        assertEquals("ROLE_ADMIN", response.getData().getRole());
        assertEquals("inactive", response.getData().getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        VerifyTokenResponse.Data data1 = new VerifyTokenResponse.Data("user3", "user3@example.com", "ROLE_MANAGER", "active");
        VerifyTokenResponse.Data data2 = new VerifyTokenResponse.Data("user3", "user3@example.com", "ROLE_MANAGER", "active");

        VerifyTokenResponse response1 = new VerifyTokenResponse(200, "OK", data1, 987654321L);
        VerifyTokenResponse response2 = new VerifyTokenResponse(200, "OK", data2, 987654321L);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }
}