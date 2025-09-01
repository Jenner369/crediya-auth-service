package co.com.crediya.r2dbc;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.r2dbc.adapter.RoleReactiveRepositoryAdapter;
import co.com.crediya.r2dbc.entity.RoleEntity;
import co.com.crediya.r2dbc.repository.RoleReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleReactiveRepositoryAdapterTest {

    @InjectMocks
    RoleReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    RoleReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private final UUID sampleId = Roles.ADMIN.getId();

    private final RoleEntity sampleEntity = new RoleEntity(
            sampleId,
            Roles.ADMIN.getName(),
            Roles.ADMIN.getDescription()
    );

    private final Role sampleRole = new Role(
            sampleId,
            Roles.ADMIN.getName(),
            Roles.ADMIN.getDescription()
    );

    @Test
    void mustFindById() {
        when(repository.findById(sampleId)).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(sampleEntity, Role.class)).thenReturn(sampleRole);

        StepVerifier.create(repositoryAdapter.findById(sampleId))
                .expectNextMatches(role -> role.equals(sampleRole))
                .verifyComplete();
    }

    @Test
    void mustFindAll() {
        when(repository.findAll()).thenReturn(Flux.just(sampleEntity));
        when(mapper.map(sampleEntity, Role.class)).thenReturn(sampleRole);

        StepVerifier.create(repositoryAdapter.findAll())
                .expectNextMatches(role -> role.equals(sampleRole))
                .verifyComplete();
    }

    @Test
    void mustSaveRole() {
        when(repository.save(any(RoleEntity.class))).thenReturn(Mono.just(sampleEntity));
        when(mapper.map(any(Role.class), eq(RoleEntity.class))).thenReturn(sampleEntity);
        when(mapper.map(sampleEntity, Role.class)).thenReturn(sampleRole);

        StepVerifier.create(repositoryAdapter.save(sampleRole))
                .expectNext(sampleRole)
                .verifyComplete();
    }
}
