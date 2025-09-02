package co.com.crediya.usecase.registeruser;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.exception.EmailAlreadyExistsException;
import co.com.crediya.exception.IdentityDocumentAlreadyExists;
import co.com.crediya.exception.RoleNotFoundException;
import co.com.crediya.model.common.gateways.PasswordEncoderGateway;
import co.com.crediya.model.common.gateways.TransactionalGateway;
import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase implements ReactiveUseCase<User, Mono<User>> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;
    private final TransactionalGateway transactionalGateway;

    @Override
    public Mono<User> execute(User user) {
        return Mono.fromRunnable(user::validate)
                .then(transactionalGateway.execute(
                        () -> validateEmailNotExists(user)
                                .flatMap(this::validateIdentityNotExists)
                                .flatMap(this::encodePassword)
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

    private Mono<User> encodePassword(User user) {
        return passwordEncoderGateway.encode(user.getPassword())
                .map(user::setEncodedPassword);
    }

    private Mono<User> assignDefaultRole(User user) {
        var roleIdMono = user.getRoleId() == null
                ? Mono
                    .just(Roles.CLIENT.getId())
                : roleRepository.findById(user.getRoleId())
                    .map(Role::getId)
                    .switchIfEmpty(Mono.error(new RoleNotFoundException()));

        return roleIdMono.map(roleId -> {
            user.setRoleId(roleId);
            return user;
        });
    }
}
