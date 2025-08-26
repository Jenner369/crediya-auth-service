package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Mono<User> findById(UUID id);
    Flux<User> findAll();

    Mono<Optional<User>> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByEmailExcludingId(String email, UUID excludeId);

    Mono<Optional<User>> findByIdentityDocument(String identityDocument);
    Mono<Boolean> existsByIdentityDocument(String identityDocument);
    Mono<Boolean> existsByIdentityDocumentExcludingId(String identityDocument, UUID excludeId);

    Mono<User> save(User user);
    Mono<Void> deleteById(UUID id);
}
