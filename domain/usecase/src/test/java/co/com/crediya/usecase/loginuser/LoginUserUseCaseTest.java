package co.com.crediya.usecase.loginuser;

import co.com.crediya.exception.InvalidCredentialsException;
import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.common.gateways.PasswordEncoderGateway;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.role.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderGateway passwordEncoderGateway;

    @InjectMocks
    private LoginUserUseCase loginUserUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .password("encodedPassword")
                .identityDocument("12345678")
                .telephone("987654321")
                .roleId(Roles.CLIENT.getId())
                .baseSalary(new BigDecimal("5000000"))
                .build();
    }

    @Test
    void shouldLoginUserSuccessfully() {
        var input = new LoginUserUseCaseInput(user.getEmail(), "Jenner123*/");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.just(user));
        when(passwordEncoderGateway.matches("Jenner123*/", user.getPassword()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(loginUserUseCase.execute(input))
                .expectNextMatches(u -> u.getEmail().equals("jenner@crediya.com"))
                .verifyComplete();

        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoderGateway).matches("Jenner123*/", user.getPassword());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        var input = new LoginUserUseCaseInput("nonexistent@crediya.com", "password");

        when(userRepository.findByEmail(input.email()))
                .thenReturn(Mono.empty());

        StepVerifier.create(loginUserUseCase.execute(input))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository).findByEmail(input.email());
        verify(passwordEncoderGateway, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionWhenPasswordDoesNotMatch() {
        var input = new LoginUserUseCaseInput(user.getEmail(), "wrongPassword");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.just(user));
        when(passwordEncoderGateway.matches("wrongPassword", user.getPassword()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(loginUserUseCase.execute(input))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoderGateway).matches("wrongPassword", user.getPassword());
    }
}
