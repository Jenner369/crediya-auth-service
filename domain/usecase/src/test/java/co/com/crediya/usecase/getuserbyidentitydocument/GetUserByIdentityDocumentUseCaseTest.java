package co.com.crediya.usecase.getuserbyidentitydocument;

import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.common.gateways.TransactionalGateway;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.getuserbyid.GetUserByIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserByIdentityDocumentUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionalGateway transactionalGateway;

    @InjectMocks
    private GetUserByIdentityDocumentUseCase getUserByIdentityDocumentUseCase;

    private User user;
    private String identityDocument;

    @BeforeEach
    void setUp() {
        identityDocument = "12345678";
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .identityDocument(identityDocument)
                .telephone("987654321")
                .roleId(Roles.CLIENT.getId())
                .baseSalary(new BigDecimal("5000000"))
                .build();
    }

    @Test
    void shouldGetUserByIdentityDocumentSuccessfully() {
        when(userRepository.findByIdentityDocument(identityDocument))
                .thenReturn(Mono.just(user));

        StepVerifier.create(getUserByIdentityDocumentUseCase.execute(identityDocument))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).findByIdentityDocument(identityDocument);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByIdentityDocument(identityDocument))
                .thenReturn(Mono.empty());

        StepVerifier.create(getUserByIdentityDocumentUseCase.execute(identityDocument))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository).findByIdentityDocument(identityDocument);
    }
}
