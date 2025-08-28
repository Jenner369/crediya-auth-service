package co.com.crediya.validation;

import co.com.crediya.api.dto.user.RegisterUserDTO;
import co.com.crediya.api.validation.DTOValidatorImp;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class DTOValidatorImpTest {

    private DTOValidatorImp dtoValidator;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        dtoValidator = new DTOValidatorImp(validator);
    }

    @Test
    void shouldPassValidationWhenValidDTO() {
        var dto = new RegisterUserDTO(
                "Jenner",
                "Durand",
                "1996-12-12",
                null,
                "jenner@crediya.com",
                "12345678",
                "98765432",
                new BigDecimal("3000000")
        );

        StepVerifier.create(dtoValidator.validate(dto))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void shouldFailValidationWhenInvalidDTO() {
        var dto = new RegisterUserDTO(
                "Jenner",
                "",
                "1996-12-12",
                null,
                "jenner@crediya.com",
                "",
                "98765432",
                new BigDecimal("3000000")
        );

        StepVerifier.create(dtoValidator.validate(dto))
                .expectError(ConstraintViolationException.class)
                .verify();
    }
}

