package co.com.crediya.api.v1;

import co.com.crediya.api.contract.TokenProvider;
import co.com.crediya.api.dto.auth.TokenDTO;
import co.com.crediya.api.dto.user.LoginUserDTO;
import co.com.crediya.api.dto.user.MinimalUserDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.api.presentation.auth.v1.AuthRouterV1;
import co.com.crediya.api.presentation.auth.v1.handler.LoginUserHandlerV1;
import co.com.crediya.api.presentation.auth.v1.handler.MeUserHandlerV1;
import co.com.crediya.api.authentication.AuthUserDetails;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.usecase.loginuser.LoginUserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {
        AuthRouterV1.class,
        LoginUserHandlerV1.class,
        MeUserHandlerV1.class,
        AuthRouterV1Test.TestConfig.class,
        AuthRouterV1Test.TestSecurityConfig.class
})
@WebFluxTest
class AuthRouterV1Test {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoginUserUseCase loginUserUseCase;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private UserDTOMapper mapper;

    private final UUID sampleId = UUID.randomUUID();

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WebProperties.Resources webPropertiesResources() {
            return new WebProperties.Resources();
        }
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex.pathMatchers("/api/v1/login").permitAll()
                            .anyExchange().authenticated())
                    .build();
        }
    }

    @BeforeEach
    void setupMocks() {
        var deadLine = new Date(System.currentTimeMillis() + 60 * 60 * 1000);

        when(tokenProvider.generateToken(any(), any(), any()))
                .thenReturn(Mono.just(
                        new TokenDTO("fake-jwt-token", "Bearer", deadLine)
                ));
    }

    @Test
    void testLoginUserSuccess() {
        var loginDTO = new LoginUserDTO("jenner@crediya.com", "Jenner123*/");
        var user = mock(co.com.crediya.model.user.User.class);
        when(user.getId()).thenReturn(sampleId);
        when(user.getEmail()).thenReturn("jenner@crediya.com");
        when(user.getRoleId()).thenReturn(UUID.randomUUID());

        when(mapper.toLoginInputFromLoginDTO(loginDTO))
                .thenReturn(new co.com.crediya.usecase.loginuser.LoginUserUseCaseInput(
                        loginDTO.email(), loginDTO.password()
                ));
        when(loginUserUseCase.execute(any())).thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDTO.class)
                .value(token -> Assertions.assertThat(token.accessToken()).isEqualTo("fake-jwt-token"));
    }

    @Test
    void testMeEndpointSuccess() {
        var testAuthUserDetails = new AuthUserDetails(
                sampleId.toString(),
                "jenner@crediya.com",
                "fake-password",
                List.of(new SimpleGrantedAuthority(Roles.CLIENT.getId().toString()))
        );

        var auth = new UsernamePasswordAuthenticationToken(
                testAuthUserDetails,
                testAuthUserDetails.getPassword(),
                testAuthUserDetails.getAuthorities()
        );

        webTestClient.mutateWith(mockAuthentication(auth))
                .get()
                .uri("/api/v1/me")
                .exchange()
                .expectStatus().isOk()
                .expectBody(MinimalUserDTO.class)
                .value(userResponse -> {
                    Assertions.assertThat(userResponse.id()).isEqualTo(testAuthUserDetails.getId());
                    Assertions.assertThat(userResponse.email()).isEqualTo(testAuthUserDetails.getUsername());
                    Assertions.assertThat(userResponse.roleId()).isEqualTo(testAuthUserDetails.getRoleId());
                });
    }
}
