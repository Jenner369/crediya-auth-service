package co.com.crediya.api.authentication.filter;

import co.com.crediya.api.authentication.AuthUserDetails;
import co.com.crediya.api.contract.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ApplicationAuthenticationManager implements ReactiveAuthenticationManager {

    private final TokenProvider tokenProvider;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        return tokenProvider.validateTokenAndGetClaims(authToken)
                .flatMap(claimsDTO ->
                        userDetailsService.findByUsername(claimsDTO.username())
                            .map(userDetails ->
                                    new AuthUserDetails(
                                            claimsDTO.userId(),
                                            userDetails.getUsername(),
                                            authToken,
                                            userDetails.getAuthorities()
                                    )
                            )
                            .map(userDetails ->
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            userDetails.getPassword(),
                                            userDetails.getAuthorities()
                                    )
                            )
                );
    }
}
