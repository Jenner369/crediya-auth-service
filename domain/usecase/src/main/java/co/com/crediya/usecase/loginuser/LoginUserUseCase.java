package co.com.crediya.usecase.loginuser;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.exception.InvalidCredentialsException;
import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.common.gateways.PasswordEncoderGateway;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUserUseCase implements ReactiveUseCase<LoginUserUseCaseInput, Mono<User>> {
    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;

    @Override
    public Mono<User> execute(LoginUserUseCaseInput input) {
        return userRepository.findByEmail(input.email())
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> validatePassword(user, input.password()));
    }

    private Mono<User> validatePassword(User user, String password) {
        return passwordEncoderGateway.matches(password, user.getPassword())
                .flatMap(matches ->
                        Boolean.TRUE.equals(matches)
                                ? Mono.just(user)
                                : Mono.error(new InvalidCredentialsException())
                );
    }
}
