package co.com.crediya.usecase.registeruser;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.exception.EmailAlreadyExistsException;
import co.com.crediya.exception.IdentityDocumentAlreadyExists;
import co.com.crediya.model.common.gateways.TransactionalGateway;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase implements ReactiveUseCase<User, Mono<User>> {

    private final UserRepository userRepository;
    private final TransactionalGateway transactionalGateway;

    @Override
    public Mono<User> execute(User user) {
        return Mono.fromRunnable(user::validate)
                .then(transactionalGateway.execute(
                        () -> validateEmailNotExists(user)
                                .flatMap(this::validateEmailNotExists)
                                .flatMap(this::validateIdentityNotExists)
                                .flatMap(this::assignDefaultRole)
                                .flatMap(userRepository::save)
                ));
    }

    private Mono<User> validateEmailNotExists(User user) {
        var email = user.getEmail();

        return userRepository.existsByEmail(email)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailAlreadyExistsException(email));
                    }

                    return Mono.just(user);
                });
    }

    private Mono<User> validateIdentityNotExists(User user) {
        var identityDocument = user.getIdentityDocument();

        return userRepository.existsByIdentityDocument(identityDocument)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IdentityDocumentAlreadyExists(identityDocument));
                    }

                    return Mono.just(user);
                });
    }

    private Mono<User> assignDefaultRole(User user) {
        user.setRoleId(Roles.CLIENT.getId());

        return Mono.just(user);
    }
}
