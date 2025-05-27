package id.ac.ui.cs.advprog.papikos.chat.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

class TokenAuthenticationFilterTest {

    private TokenAuthenticationFilter filter;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    private final String authBaseUrl = "http://auth-service/api/v1";
    private final String validInternalToken = "test_internal_token";

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        filter = new TokenAuthenticationFilter(restTemplate, objectMapper);
        ReflectionTestUtils.setField(filter, "authVerifyUrl", authBaseUrl);
        ReflectionTestUtils.setField(filter, "internalTokenSecret", validInternalToken);
    }

    @Test
    void bearerTokenNullResponseTest() {
        String bearerToken = "valid_bearer_token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + bearerToken);
        request.setRequestURI("/bearer-null");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();

        Mockito.when(restTemplate.exchange(
                eq(authBaseUrl + "/verify"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            filter.doFilterInternal(request, response, chain);
        });
    }
}