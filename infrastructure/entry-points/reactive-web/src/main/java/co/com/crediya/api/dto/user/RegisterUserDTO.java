package co.com.crediya.api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@Schema(description = "Datos necesarios para registrar un nuevo usuario")
public record RegisterUserDTO(

        @Schema(description = "Nombre del usuario", example = "Jenner")
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @Schema(description = "Apellido del usuario", example = "Durand")
        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @Schema(description = "Fecha de nacimiento del usuario en formato AAAA-MM-DD", example = "1990-05-15")
        String birthDate,

        @Schema(description = "Dirección del usuario", example = "Calle 123 #45-67")
        String address,

        @Schema(description = "Email del usuario", example = "admin@pragma.co")
        @Email(message = "El email debe ser válido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @Schema(description = "Clave del usuario", example = "P@ssw0rd!")
        @NotBlank(message = "La clave es obligatoria")
        String password,

        @Schema(description = "Documento de identidad del usuario", example = "78876789")
        @NotBlank(message = "El documento de identidad es obligatorio")
        String identityDocument,

        @Schema(description = "Teléfono del usuario", example = "3001234567")
        String telephone,

        @Schema(description = "Salario base del usuario", example = "2500000")
        @NotNull(message = "El salario base es obligatorio")
        BigDecimal baseSalary,

        @Schema(description = "ID del rol asignado al usuario", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String roleId
) {
}
