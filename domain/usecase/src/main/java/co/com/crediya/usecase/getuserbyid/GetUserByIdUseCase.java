package co.com.crediya.usecase.getuserbyid;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.exception.UserNotFoundException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class GetUserByIdUseCase implements ReactiveUseCase<UUID, Mono<User>> {

    private final UserRepository userRepository;

    @Override
    public Mono<User> execute(UUID id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }
}
