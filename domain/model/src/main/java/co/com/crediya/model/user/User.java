package co.com.crediya.model.user;
import co.com.crediya.model.user.exceptions.*;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private UUID id;
    private String name;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String email;
    private String password;
    private String identityDocument;
    private String telephone;
    private UUID roleId;
    private BigDecimal baseSalary;

    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    public void validate() {
        validateName(this.name);
        validateLastName(this.lastName);
        validateEmail(this.email);
        validateBaseSalary(this.baseSalary);
        validatePassword(this.password);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new NameRequiredException();
        }
    }

    private void validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new LastNameRequiredException();
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new EmailInvalidException();
        }
    }

    private void validateBaseSalary(BigDecimal baseSalary) {
        if (baseSalary == null || baseSalary.compareTo(BigDecimal.ZERO) < 0
                || baseSalary.compareTo(MAX_SALARY) > 0) {
            throw new SalaryOutOfRangeException(BigDecimal.ZERO, MAX_SALARY);
        }
    }

    private void validatePassword(String password) {
        if (
                password == null ||
                password.length() < 8
        ) {
            throw new PasswordInvalidException();
        }
    }

    public User setEncodedPassword(String encodedPassword) {
        password = encodedPassword;

        return this;
    }
}
