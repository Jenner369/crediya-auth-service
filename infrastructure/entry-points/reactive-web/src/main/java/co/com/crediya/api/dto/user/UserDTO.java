package co.com.crediya.api.dto.user;

import java.math.BigDecimal;

public record UserDTO(
        String id,
        String name,
        String lastName,
        String birthDate,
        String address,
        String email,
        String identityDocument,
        String telephone,
        String roleId,
        BigDecimal baseSalary
) {
}
