package co.com.crediya.model.user;

import static org.junit.jupiter.api.Assertions.*;

import co.com.crediya.model.user.exceptions.EmailInvalidException;
import co.com.crediya.model.user.exceptions.LastNameRequiredException;
import co.com.crediya.model.user.exceptions.NameRequiredException;
import co.com.crediya.model.user.exceptions.SalaryOutOfRangeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class UserTest {
    @Test
    void shouldValidateSuccessfullyWithValidData() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .identityDocument("12345678")
                .telephone("987654321")
                .roleId(UUID.randomUUID())
                .baseSalary(new BigDecimal("5000000"))
                .build();

        assertDoesNotThrow(user::validate);
        assertEquals("Jenner", user.getName());
        assertEquals("Durand", user.getLastName());
    }

    @Test
    void shouldThrowNameRequiredExceptionWhenNameIsMissing() {
        User user = User.builder()
                .lastName("Durand")
                .email("jenner@crediya.com")
                .baseSalary(BigDecimal.valueOf(1000))
                .build();

        assertThrows(NameRequiredException.class, user::validate);
    }

    @Test
    void shouldThrowLastNameRequiredExceptionWhenLastNameIsMissing() {
        User user = User.builder()
                .name("Jenner")
                .email("jenner@crediya.com")
                .baseSalary(BigDecimal.valueOf(1000))
                .build();

        assertThrows(LastNameRequiredException.class, user::validate);
    }

    @Test
    void shouldThrowEmailInvalidExceptionWhenEmailIsInvalid() {
        User user = User.builder()
                .name("Jenner")
                .lastName("Durand")
                .email("crediya.email.com")
                .baseSalary(BigDecimal.valueOf(1000))
                .build();

        assertThrows(EmailInvalidException.class, user::validate);
    }

    @Test
    void shouldThrowEmailInvalidExceptionWhenEmailIsNull() {
        User user = User.builder()
                .name("Jenner")
                .lastName("Durand")
                .email(null)
                .baseSalary(BigDecimal.valueOf(1000))
                .build();

        assertThrows(EmailInvalidException.class, user::validate);
    }

    @Test
    void shouldThrowSalaryOutOfRangeExceptionWhenSalaryIsNegative() {
        User user = User.builder()
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .baseSalary(BigDecimal.valueOf(-100))
                .build();

        assertThrows(SalaryOutOfRangeException.class, user::validate);
    }

    @Test
    void shouldThrowSalaryOutOfRangeExceptionWhenSalaryIsTooHigh() {
        User user = User.builder()
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .baseSalary(new BigDecimal("20000000"))
                .build();

        assertThrows(SalaryOutOfRangeException.class, user::validate);
    }

    @Test
    void shouldThrowSalaryOutOfRangeExceptionWhenSalaryIsNull() {
        User user = User.builder()
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .baseSalary(null)
                .build();

        assertThrows(SalaryOutOfRangeException.class, user::validate);
    }
}
