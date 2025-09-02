package co.com.crediya.provider.jwt;

import co.com.crediya.api.dto.auth.TokenDTO;
import co.com.crediya.api.dto.common.ClaimsDTO;
import co.com.crediya.api.provider.jwt.JwtProperties;
import co.com.crediya.api.provider.jwt.JwtTokenProvider;
import co.com.crediya.model.role.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setup() {
        var props = new JwtProperties();
        props.setSecret("my-super-secret-key-that-is-long-enough-1234567890");
        props.setExpirationHours(2);
        tokenProvider = new JwtTokenProvider(props);
    }

    @Test
    void shouldCreateValidToken() {
        var userId = "1234";
        var username = "test@crediya.com";
        var roleId = Roles.CLIENT.getId().toString();

        var result = tokenProvider.generateToken(userId, username, roleId);

        StepVerifier.create(result)
                .assertNext(tokenDTO -> {
                    assertThat(tokenDTO).isInstanceOf(TokenDTO.class);
                    assertThat(tokenDTO.accessToken()).isNotBlank();
                    assertThat(tokenDTO.tokenType()).isEqualTo("Bearer");
                    assertThat(tokenDTO.expiresAt()).isAfter(new Date());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnClaimsWhenTokenIsValid() {
        var userId = "1234";
        var username = "test@crediya.com";
        var roleId = Roles.CLIENT.getId().toString();

        var tokenDTO = tokenProvider.generateToken(userId, username, roleId).block();
        assert tokenDTO != null;

        var result = tokenProvider.validateTokenAndGetClaims(tokenDTO.accessToken());

        StepVerifier.create(result)
                .assertNext(claims -> {
                    assertThat(claims).isInstanceOf(ClaimsDTO.class);
                    assertThat(claims.userId()).isEqualTo(userId);
                    assertThat(claims.username()).isEqualTo(username);
                    assertThat(claims.roleId()).isEqualTo(roleId);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenTokenIsExpired() {
        var expiredProps = new JwtProperties();
        expiredProps.setSecret("my-super-secret-key-that-is-long-enough-1234567890");
        expiredProps.setExpirationHours(-1);
        var expiredProvider = new JwtTokenProvider(expiredProps);

        var tokenDTO = expiredProvider.generateToken("1", "expired@crediya.com", "ROLE_ADMIN").block();
        assert tokenDTO != null;

        var result = expiredProvider.validateTokenAndGetClaims(tokenDTO.accessToken());

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenTokenIsInvalid() {
        var invalidToken = "this.is.not.a.jwt";

        var result = tokenProvider.validateTokenAndGetClaims(invalidToken);

        StepVerifier.create(result)
                .verifyComplete();
    }
}
