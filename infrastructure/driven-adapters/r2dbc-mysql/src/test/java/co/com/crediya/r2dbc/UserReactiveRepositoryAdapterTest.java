package co.com.crediya.r2dbc;

import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.r2dbc.adapter.UserReactiveRepositoryAdapter;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.repository.UserReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {
    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private final UUID sampleId = UUID.randomUUID();
    private final UserEntity sampleEntity
            = new UserEntity(
                    sampleId,
                "Jenner",
                "Durand",
                LocalDate.of(1990,12,12),
                null,
                "jenner@crediya.com",
                "Jenner123*/",
                "123456",
                "98697642",
                Roles.ADMIN.getId(),
                new BigDecimal("500000")
    );
    private final User sampleUser
            = new User(
                    sampleId,
            "Jenner",
            "Durand",
            LocalDate.of(1990,12,12),
            null,
            "jenner@crediya.com",
            "Jenner123*/",
            "123456",
            "98697642",
            Roles.ADMIN.getId(),
            new BigDecimal("500000")
    );


    @Test
    void mustFindById() {
        when(repository.findById(sampleId)).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(sampleEntity, User.class)).thenReturn(sampleUser);

        Mono<User> result = repositoryAdapter.findById(sampleId);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(sampleUser))
                .verifyComplete();
    }

    @Test
    void mustFindAll() {
        when(repository.findAll()).thenReturn(Flux.just(sampleEntity));
        when(mapper.map(sampleEntity, User.class)).thenReturn(sampleUser);

        StepVerifier.create(repositoryAdapter.findAll())
                .expectNextMatches(value -> value.equals(sampleUser))
                .verifyComplete();
    }

    @Test
    void mustFindByEmail() {
        when(repository.findByEmail("jenner@crediya.com")).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(sampleEntity, User.class)).thenReturn(sampleUser);

        StepVerifier.create(repositoryAdapter.findByEmail("jenner@crediya.com"))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenEmailNotFound() {
        when(repository.findByEmail("unknown@crediya.com")).thenReturn(Mono.empty());

        StepVerifier.create(repositoryAdapter.findByEmail("unknown@crediya.com"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void mustCheckExistsByEmail() {
        when(repository.existsByEmail("jenner@crediya.com")).thenReturn(Mono.just(true));

        StepVerifier.create(repositoryAdapter.existsByEmail("jenner@crediya.com"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void mustCheckExistsByEmailExcludingId() {
        when(repository.existsByEmailAndIdNot("jenner@crediya.com", sampleId)).thenReturn(Mono.just(false));

        StepVerifier.create(repositoryAdapter.existsByEmailExcludingId("jenner@crediya.com", sampleId))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void mustFindByIdentityDocument() {
        when(repository.findByIdentityDocument("123456")).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(sampleEntity, User.class)).thenReturn(sampleUser);

        StepVerifier.create(repositoryAdapter.findByIdentityDocument("123456"))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void mustFindAllByIdentityDocuments() {
        var user1 = User.builder()
                .id(UUID.randomUUID())
                .name("Jenner")
                .lastName("Durand")
                .email("jenner@crediya.com")
                .identityDocument("12345678")
                .telephone("987654321")
                .roleId(Roles.CLIENT.getId())
                .baseSalary(new BigDecimal("50000"))
                .build();

        var userEntity1 = new UserEntity(
                user1.getId(),
                user1.getName(),
                user1.getLastName(),
                user1.getBirthDate(),
                user1.getAddress(),
                user1.getEmail(),
                user1.getPassword(),
                user1.getIdentityDocument(),
                user1.getTelephone(),
                user1.getRoleId(),
                user1.getBaseSalary()
        );

        var user2 = User.builder()
                .id(UUID.randomUUID())
                .name("Carlos")
                .lastName("Perez")
                .email("carlos@crediya.com")
                .identityDocument("87654321")
                .telephone("123456789")
                .roleId(Roles.ADVISOR.getId())
                .baseSalary(new BigDecimal("70000"))
                .build();

        var userEntity2 = new UserEntity(
                user2.getId(),
                user2.getName(),
                user2.getLastName(),
                user2.getBirthDate(),
                user2.getAddress(),
                user2.getEmail(),
                user2.getPassword(),
                user2.getIdentityDocument(),
                user2.getTelephone(),
                user2.getRoleId(),
                user2.getBaseSalary()
        );

        when(mapper.map(userEntity1, User.class)).thenReturn(user1);
        when(mapper.map(userEntity2, User.class)).thenReturn(user2);

        var identityDocuments = List.of("12345678", "87654321");
        when(repository.findAllByIdentityDocumentIn(identityDocuments))
                .thenReturn(Flux.just(userEntity1, userEntity2));

        StepVerifier.create(repositoryAdapter.findAllByIdentityDocuments(identityDocuments))
                .expectNextMatches(userResponse1 -> userResponse1.getId().equals(user1.getId()))
                .expectNextMatches(userResponse2 -> userResponse2.getId().equals(user2.getId()))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdentityDocumentNotFound() {
        when(repository.findByIdentityDocument("000000")).thenReturn(Mono.empty());

        StepVerifier.create(repositoryAdapter.findByIdentityDocument("000000"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void mustCheckExistsByIdentityDocument() {
        when(repository.existsByIdentityDocument("123456")).thenReturn(Mono.just(true));

        StepVerifier.create(repositoryAdapter.existsByIdentityDocument("123456"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void mustCheckExistsByIdentityDocumentExcludingId() {
        when(repository.existsByIdentityDocumentAndIdNot("123456", sampleId))
                .thenReturn(Mono.just(false));

        StepVerifier.create(repositoryAdapter.existsByIdentityDocumentExcludingId("123456", sampleId))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void mustSaveUser() {
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(sampleEntity);
        when(mapper.map(sampleEntity, User.class)).thenReturn(sampleUser);

        StepVerifier.create(repositoryAdapter.save(sampleUser))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void mustDeleteById() {
        when(repository.deleteById(sampleId)).thenReturn(Mono.empty());

        StepVerifier.create(repositoryAdapter.deleteById(sampleId))
                .verifyComplete();
    }
}
