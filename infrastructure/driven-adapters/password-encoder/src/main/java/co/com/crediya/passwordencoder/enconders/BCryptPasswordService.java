package co.com.crediya.passwordencoder.enconders;

import co.com.crediya.model.common.gateways.PasswordEncoderGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BCryptPasswordService implements PasswordEncoderGateway {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Mono<String> encode(String rawPassword) {
        return Mono.just(encoder.encode(rawPassword));
    }

    @Override
    public Mono<Boolean> matches(String rawPassword, String encodedPassword) {
        return Mono.just(encoder.matches(rawPassword, encodedPassword));
    }
}
