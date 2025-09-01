package co.com.crediya.api.contract;

import co.com.crediya.api.dto.common.ClaimsDTO;
import co.com.crediya.api.dto.auth.TokenDTO;
import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<TokenDTO> generateToken(String userId, String email, String roleId);

    Mono<ClaimsDTO> validateTokenAndGetClaims(String token);
}
