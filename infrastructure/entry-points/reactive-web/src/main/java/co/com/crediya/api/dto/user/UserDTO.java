package co.com.crediya.api.dto.user;

import lombok.Getter;

public record UserDTO(
        String id,
        String name,
        String lastName,
        String email,
        String identityDocument,
        String telephone,
        String roleId
) {
}
