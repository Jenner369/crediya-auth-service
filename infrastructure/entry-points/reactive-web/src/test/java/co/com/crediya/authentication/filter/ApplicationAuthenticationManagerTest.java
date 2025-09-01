package co.com.crediya.authentication.filter;

import co.com.crediya.api.authentication.AuthUserDetails;
import co.com.crediya.api.authentication.filter.ApplicationAuthenticationManager;
import co.com.crediya.api.contract.TokenProvider;
import co.com.crediya.model.role.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class ApplicationAuthenticationManagerTest {

    private TokenProvider tokenProvider;
    private ReactiveUserDetailsService userDetailsService;
    private ApplicationAuthenticationManager authManager;

    @BeforeEach
    void setUp() {
        tokenProvider = Mockito.mock(TokenProvider.class);
        userDetailsService = Mockito.mock(ReactiveUserDetailsService.class);
        authManager = new ApplicationAuthenticationManager(tokenProvider, userDetailsService);
    }

    @Test
    void shouldReturnAuthenticationWhenTokenIsValid() {
        var token = "valid-token";
        var username = "user@test.com";

        var claimsDTO = Mockito.mock(co.com.crediya.api.dto.common.ClaimsDTO.class);
        when(claimsDTO.userId()).thenReturn("user-id");
        when(claimsDTO.username()).thenReturn(username);
        when(claimsDTO.roleId()).thenReturn(Roles.CLIENT.getId().toString());

        when(tokenProvider.validateTokenAndGetClaims(token)).thenReturn(Mono.just(claimsDTO));

        var springUser = User.withUsername(username)
                .password("password")
                .authorities(Roles.CLIENT.getId().toString())
                .build();

        when(userDetailsService.findByUsername(username)).thenReturn(Mono.just(springUser));

        var authentication = new UsernamePasswordAuthenticationToken(token, token);

        StepVerifier.create(authManager.authenticate(authentication))
                .assertNext(auth -> {
                    var principal = (AuthUserDetails) auth.getPrincipal();
                    assert principal.getId().equals("user-id");
                    assert principal.getUsername().equals(username);
                    assert principal.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals(Roles.CLIENT.getId().toString()));
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenTokenIsInvalid() {
        var token = "invalid-token";
        when(tokenProvider.validateTokenAndGetClaims(token)).thenReturn(Mono.empty());

        var authentication = new UsernamePasswordAuthenticationToken(token, token);

        StepVerifier.create(authManager.authenticate(authentication))
                .expectNextCount(0)
                .verifyComplete();
    }
}
