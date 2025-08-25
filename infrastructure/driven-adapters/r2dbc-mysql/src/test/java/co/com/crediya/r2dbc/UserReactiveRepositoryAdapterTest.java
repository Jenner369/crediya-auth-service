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
            = new UserEntity(sampleId, "Jenner", "Durand", "jenner@crediya.com", "123456", "98697642", Roles.ADMIN.getId(), new BigDecimal("500000"));
    private final User sampleUser
            = new User(sampleId, "Jenner", "Durand", "jenner@crediya.com", "123456", "98697642", Roles.ADMIN.getId(), new BigDecimal("500000"));


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
        when(repository.findByEmail("jenner@crediya.com")).thenReturn(Mono.just(Optional.of(sampleEntity)));
        when(mapper.map(sampleEntity, User.class)).thenReturn(sampleUser);

        StepVerifier.create(repositoryAdapter.findByEmail("jenner@crediya.com"))
                .expectNext(Optional.of(sampleUser))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenEmailNotFound() {
        when(repository.findByEmail("unknown@crediya.com")).thenReturn(Mono.empty());

        StepVerifier.create(repositoryAdapter.findByEmail("unknown@crediya.com"))
                .expectNext(Optional.empty())
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
        when(repository.findByIdentityDocument("123456")).thenReturn(Mono.just(Optional.of(sampleEntity)));
        when(mapper.map(sampleEntity, User.class)).thenReturn(sampleUser);

        StepVerifier.create(repositoryAdapter.findByIdentityDocument("123456"))
                .expectNext(Optional.of(sampleUser))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdentityDocumentNotFound() {
        when(repository.findByIdentityDocument("000000")).thenReturn(Mono.empty());

        StepVerifier.create(repositoryAdapter.findByIdentityDocument("000000"))
                .expectNext(Optional.empty())
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
