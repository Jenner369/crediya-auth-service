package co.com.crediya.usecase.listusersbyidentitydocuments;

import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class ListUsersByIdentityDocumentsUseCase implements ReactiveUseCase<List<String>, Flux<User>> {

    private final UserRepository userRepository;

    @Override
    public Flux<User> execute(List<String> identityDocuments) {
        return userRepository.findAllByIdentityDocuments(identityDocuments);
    }
}
