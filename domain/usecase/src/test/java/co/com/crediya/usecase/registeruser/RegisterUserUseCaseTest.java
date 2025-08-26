package co.com.crediya.usecase.registeruser;

import co.com.crediya.exception.EmailAlreadyExistsException;
import co.com.crediya.exception.IdentityDocumentAlreadyExists;
import co.com.crediya.model.common.gateways.TransactionalGateway;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionalGateway transactionalGateway;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .identityDocument("12345678")
                .telephone("987654321")
                .roleId(Roles.CLIENT.getId())
                .baseSalary(new BigDecimal("5000000"))
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(user.getIdentityDocument())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(transactionalGateway.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<Mono<?>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        StepVerifier.create(registerUserUseCase.execute(user))
                .expectNextMatches(u -> u.getEmail().equals("jenner@crediya.com"))
                .verifyComplete();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));
        when(transactionalGateway.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<Mono<?>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        StepVerifier.create(registerUserUseCase.execute(user))
                .expectError(EmailAlreadyExistsException.class)
                .verify();
    }

    @Test
    void shouldThrowWhenIdentityAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(user.getIdentityDocument())).thenReturn(Mono.just(true));
        when(transactionalGateway.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<Mono<?>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        StepVerifier.create(registerUserUseCase.execute(user))
                .expectError(IdentityDocumentAlreadyExists.class)
                .verify();
    }
}
