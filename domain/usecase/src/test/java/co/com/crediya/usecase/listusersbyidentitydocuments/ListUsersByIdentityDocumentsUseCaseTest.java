package co.com.crediya.usecase.listusersbyidentitydocuments;

import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListUsersByIdentityDocumentsUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListUsersByIdentityDocumentsUseCase listUsersByIdentityDocumentsUseCase;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .identityDocument("12345678")
                .telephone("987654321")
                .roleId(Roles.CLIENT.getId())
                .baseSalary(new BigDecimal("50000"))
                .build();

        user2 = User.builder()
                .id(UUID.randomUUID())
                .name("Carlos")
                .lastName("Perez")
                .email("carlos@crediya.com")
                .identityDocument("87654321")
                .telephone("123456789")
                .roleId(Roles.ADVISOR.getId())
                .baseSalary(new BigDecimal("70000"))
                .build();
    }

    @Test
    void shouldReturnUsersWhenIdentityDocumentsMatch() {
        var documents = List.of("12345678", "87654321");

        when(userRepository.findAllByIdentityDocuments(documents))
                .thenReturn(Flux.just(user1, user2));

        StepVerifier.create(listUsersByIdentityDocumentsUseCase.execute(documents))
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();

        verify(userRepository).findAllByIdentityDocuments(documents);
    }

    @Test
    void shouldReturnEmptyWhenNoUsersFound() {
        var documents = List.of("00000000");

        when(userRepository.findAllByIdentityDocuments(documents))
                .thenReturn(Flux.empty());

        StepVerifier.create(listUsersByIdentityDocumentsUseCase.execute(documents))
                .verifyComplete();

        verify(userRepository).findAllByIdentityDocuments(documents);
    }
}
