package co.com.crediya.api.dto.auth;

import java.util.Date;

public record TokenDTO(
        String accessToken,
        String tokenType,
        Date expiresAt
) {
}
