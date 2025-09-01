package co.com.crediya.authentication;

import co.com.crediya.api.authentication.ReactiveUserDetailsServiceImpl;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

class ReactiveUserDetailsServiceImplTest {

    private UserRepository userRepository;
    private ReactiveUserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new ReactiveUserDetailsServiceImpl(userRepository);
    }

    @Test
    void shouldReturnUserDetailsWhenUserExists() {
        var email = "user@test.com";
        var password = "password";

        var user = mock(User.class);
        when(user.getEmail()).thenReturn(email);
        when(user.getPassword()).thenReturn(password);
        when(user.getRoleId()).thenReturn(Roles.CLIENT.getId());

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));

        Mono<UserDetails> result = userDetailsService.findByUsername(email);

        StepVerifier.create(result)
                .assertNext(userDetails -> {
                    assert userDetails.getUsername().equals(email);
                    assert userDetails.getPassword().equals(password);
                    assert userDetails.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals(Roles.CLIENT.getId().toString()));
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        var email = "notfound@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());

        StepVerifier.create(userDetailsService.findByUsername(email))
                .verifyComplete();
    }
}
