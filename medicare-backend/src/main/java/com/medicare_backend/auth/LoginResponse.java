
package com.medicare_backend.auth;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        String username,
        String role
) {
}
