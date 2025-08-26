package co.com.crediya.r2dbc.repository;

import co.com.crediya.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, UUID>, ReactiveQueryByExampleExecutor<UserEntity> {
    Mono<UserEntity> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByEmailAndIdNot(String email, UUID excludeId);

    Mono<UserEntity> findByIdentityDocument(String identityDocument);
    Mono<Boolean> existsByIdentityDocument(String identityDocument);
    Mono<Boolean> existsByIdentityDocumentAndIdNot(String identityDocument, UUID excludeId);
}
