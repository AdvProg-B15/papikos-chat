package id.ac.ui.cs.advprog.papikos.chat.util;

import java.util.UUID;
import org.springframework.security.core.Authentication;

public class AuthenticationUtil {

    /**
     * Helper method to extract UUID from Authentication principal name.
     * Throws an IllegalArgumentException if parsing fails.
     */
    public static UUID getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("Authentication principal is required but missing.");
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing UUID from principal name: " + authentication.getName());
            throw new IllegalArgumentException("Invalid user identifier format in authentication token.");
        }
    }
}