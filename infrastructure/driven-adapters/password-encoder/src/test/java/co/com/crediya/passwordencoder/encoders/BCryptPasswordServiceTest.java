package co.com.crediya.passwordencoder.encoders;

import co.com.crediya.passwordencoder.enconders.BCryptPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class BCryptPasswordServiceTest {

    private BCryptPasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new BCryptPasswordService();
    }

    @Test
    void shouldEncodePassword() {
        String rawPassword = "Jenner123*/";

        StepVerifier.create(passwordService.encode(rawPassword))
                .expectNextMatches(encoded -> !encoded.equals(rawPassword) && !encoded.isEmpty())
                .verifyComplete();
    }

    @Test
    void shouldMatchEncodedPassword() {
        String rawPassword = "Jenner123*/";

        StepVerifier.create(passwordService.encode(rawPassword))
                .assertNext(encodedPassword -> {
                    StepVerifier.create(passwordService.matches(rawPassword, encodedPassword))
                            .expectNext(true)
                            .verifyComplete();

                    StepVerifier.create(passwordService.matches("wrongPassword", encodedPassword))
                            .expectNext(false)
                            .verifyComplete();
                })
                .verifyComplete();
    }
}
