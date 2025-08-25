package co.com.crediya.r2dbc.adapter;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.repository.UserReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        UUID,
        UserReactiveRepository
> implements UserRepository {

    public UserReactiveRepositoryAdapter(
            UserReactiveRepository repository,
            ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<Optional<User>> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(userEntity -> userEntity.map(this::toEntity))
                .defaultIfEmpty(Optional.empty());
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByEmailExcludingId(String email, UUID excludeId) {
        return repository.existsByEmailAndIdNot(email, excludeId);
    }

    @Override
    public Mono<Optional<User>> findByIdentityDocument(String identityDocument) {
        return repository.findByIdentityDocument(identityDocument)
                .map(userEntity -> userEntity.map(this::toEntity))
                .defaultIfEmpty(Optional.empty());
    }

    @Override
    public Mono<Boolean> existsByIdentityDocument(String identityDocument) {
        return repository.existsByIdentityDocument(identityDocument);
    }

    @Override
    public Mono<Boolean> existsByIdentityDocumentExcludingId(String identityDocument, UUID excludeId) {
        return repository.existsByIdentityDocumentAndIdNot(identityDocument, excludeId);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
