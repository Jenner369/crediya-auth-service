package co.com.crediya.api.dto.user;

public record UserDTO(
        String id,
        String name,
        String lastName,
        String birthDate,
        String address,
        String email,
        String identityDocument,
        String telephone,
        String roleId
) {
}
