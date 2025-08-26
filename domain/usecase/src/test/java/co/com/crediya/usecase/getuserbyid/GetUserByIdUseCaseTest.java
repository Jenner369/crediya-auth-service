package co.com.crediya.usecase.getuserbyid;

import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.common.gateways.TransactionalGateway;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
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
class GetUserByIdUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionalGateway transactionalGateway;

    @InjectMocks
    private GetUserByIdUseCase getUserByIdUseCase;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
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
    void shouldGetUserByIdDocumentSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(getUserByIdUseCase.execute(userId))
                .expectNextMatches(u -> u.getEmail().equals("jenner@crediya.com"))
                .verifyComplete();

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(getUserByIdUseCase.execute(userId))
                .expectErrorMatches(UserNotFoundException.class::isInstance)
                .verify();

        verify(userRepository).findById(userId);
    }
}
