package co.com.crediya.api.provider.jwt;

import co.com.crediya.api.contract.TokenProvider;
import co.com.crediya.api.dto.common.ClaimsDTO;
import co.com.crediya.api.dto.auth.TokenDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

    private final JwtProperties properties;

    public Mono<TokenDTO> generateToken(String userId, String username, String roleId) {
        var nowDate = Date.from(Instant.now());
        var deadlineDate = Date.from(Instant.now().plus(properties.getExpirationHours(), ChronoUnit.HOURS));
        var token = Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roleId", roleId)
                .setIssuedAt(nowDate)
                .setExpiration(deadlineDate)
                .signWith(Keys.hmacShaKeyFor(properties.getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
        var dto = new TokenDTO(token, "Bearer", deadlineDate);

        return Mono.just(dto);
    }

    public Mono<ClaimsDTO> validateTokenAndGetClaims(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(properties.getSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(Date.from(Instant.now()))) {
                return Mono.empty();
            }

            return Mono.just(
                    new ClaimsDTO(
                        claims.get("userId", String.class),
                        claims.getSubject(),
                        claims.get("roleId", String.class)
                    )
            );
        } catch (Exception e) {

            return Mono.empty();
        }
    }
}