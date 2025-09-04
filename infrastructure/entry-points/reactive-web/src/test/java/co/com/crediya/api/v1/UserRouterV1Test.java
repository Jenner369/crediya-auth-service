package co.com.crediya.api.v1;

import co.com.crediya.api.contract.RoleValidator;
import co.com.crediya.api.contract.TokenProvider;
import co.com.crediya.api.dto.common.ClaimsDTO;
import co.com.crediya.api.dto.user.RegisterUserDTO;
import co.com.crediya.api.dto.user.SearchListUsersByIdentityDocumentsDTO;
import co.com.crediya.api.dto.user.UserDTO;
import co.com.crediya.api.exception.GlobalErrorAttributes;
import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.api.contract.DTOValidator;
import co.com.crediya.api.contract.UUIDValidator;
import co.com.crediya.api.presentation.auth.v1.handler.LoginUserHandlerV1;
import co.com.crediya.api.presentation.user.v1.UserRouterV1;
import co.com.crediya.api.presentation.user.v1.handler.GetUserByIdHandlerV1;
import co.com.crediya.api.presentation.user.v1.handler.GetUserByIdentityDocumentHandlerV1;
import co.com.crediya.api.presentation.user.v1.handler.ListUsersByIdentityDocumentsHandlerV1;
import co.com.crediya.api.presentation.user.v1.handler.RegisterUserHandlerV1;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.getuserbyid.GetUserByIdUseCase;
import co.com.crediya.usecase.getuserbyidentitydocument.GetUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.listusersbyidentitydocuments.ListUsersByIdentityDocumentsUseCase;
import co.com.crediya.usecase.loginuser.LoginUserUseCase;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ContextConfiguration(classes = {
        GlobalExceptionHandler.class,
        GlobalErrorAttributes.class,
        UserRouterV1.class,
        RegisterUserHandlerV1.class,
        GetUserByIdHandlerV1.class,
        LoginUserHandlerV1.class,
        GetUserByIdentityDocumentHandlerV1.class,
        ListUsersByIdentityDocumentsHandlerV1.class,
        UserRouterV1Test.TestConfig.class,
        UserRouterV1Test.TestSecurityConfig.class
})
@WebFluxTest
class UserRouterV1Test {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUserUseCase loginUserUseCase;

    @MockitoBean
    private GetUserByIdUseCase getUserByIdUseCase;

    @MockitoBean
    private GetUserByIdentityDocumentUseCase getUserByIdentityDocumentUseCase;

    @MockitoBean
    private ListUsersByIdentityDocumentsUseCase listUsersByIdentityDocumentsUseCase;

    @MockitoBean
    private UserDTOMapper userDTOMapper;

    @MockitoBean
    private DTOValidator dtoValidator;

    @MockitoBean
    private UUIDValidator uuidValidator;

    @MockitoBean
    private RoleValidator roleValidator;

     @MockitoBean
     private TokenProvider tokenProvider;

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
        public ReactiveAuthenticationManager reactiveAuthenticationManager() {
            return authentication -> Mono.just(new UsernamePasswordAuthenticationToken(
                    "test-user",
                    null,
                    List.of(new SimpleGrantedAuthority(Roles.ADMIN.getId().toString()))
            ));
        }

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(
                ServerHttpSecurity http,
                ReactiveAuthenticationManager authManager
        ) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex.anyExchange().permitAll())
                    .authenticationManager(authManager)
                    .build();
        }
    }

    @BeforeEach
    void setupTokenProviderMock() {
        when(tokenProvider.validateTokenAndGetClaims("fake-token"))
                .thenReturn(Mono.just(new ClaimsDTO(
                        "user-id-123",
                        "test-user",
                        Roles.ADMIN.getId().toString()
                )));
    }

    @Test
    void testRegisterUserSuccess() {
        var sampleId = UUID.randomUUID();
        var dto = new RegisterUserDTO(
                "Jenner",
                "Durand",
                "1996-12-12",
                null,
                "jenner@crediya.com",
                "Jenner123*/",
                "12345678",
                "98765432",
                new BigDecimal("3000000"),
                null
        );

        var user = new User(
                sampleId,
                "Jenner",
                "Durand",
                LocalDate.of(1996, 12, 12),
                null,
                "jenner@crediya.com",
                "Jenner123*/",
                "12345678",
                "98765432",
                Roles.CLIENT.getId(),
                new BigDecimal("3000000")
        );

        var responseDTO = new UserDTO(
                sampleId.toString(),
                "Jenner",
                "Durand",
                "1996-12-12",
                null,
                "jenner@crediya.com",
                "12345678",
                "98765432",
                Roles.CLIENT.getId().toString(),
                new BigDecimal("3000000")
        );

        when(dtoValidator.validate(dto)).thenReturn(Mono.just(dto));
        when(userDTOMapper.toModelFromRegisterDTO(dto)).thenReturn(user);
        when(roleValidator.validateRole(any(Roles[].class))).thenReturn(Mono.empty());
        when(registerUserUseCase.execute(user)).thenReturn(Mono.just(user));
        when(userDTOMapper.toUserDTOFromModel(user)).thenReturn(responseDTO);

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .value(userResponse -> {
                    Assertions
                            .assertThat(userResponse.email())
                            .isEqualTo("jenner@crediya.com");
                });
    }

    @Test
    void testGetUserByIdSuccess() {
        var sampleId = UUID.randomUUID();
        var user = new User(
                sampleId,
                "Jenner",
                "Durand",
                LocalDate.of(1991, 1, 1),
                "Calle 123 # 45-67",
                "jenner@crediya.com",
                "Jenner123*/",
                "12345678",
                "98765432",
                Roles.CLIENT.getId(),
                new BigDecimal("3000000")
        );

        var responseDTO = new UserDTO(
                sampleId.toString(),
                "Jenner",
                "Durand",
                "1991-01-01",
                "Calle 123 # 45-67",
                "jenner@crediya.com",
                "12345678",
                "98765432",
                Roles.CLIENT.getId().toString(),
                new BigDecimal("3000000")
        );

        when(uuidValidator.validate(sampleId.toString())).thenReturn(Mono.just(sampleId));
        when(getUserByIdUseCase.execute(sampleId)).thenReturn(Mono.just(user));
        when(userDTOMapper.toUserDTOFromModel(user)).thenReturn(responseDTO);

        webTestClient.get()
                .uri("/api/v1/usuarios/{id}", sampleId.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .value(userResponse -> {
                    Assertions
                            .assertThat(userResponse.email())
                            .isEqualTo("jenner@crediya.com");
                });
    }

    @Test
    void testGetUserByIdentityDocumentSuccess() {
        var identityDocument = "12345678";
        var sampleId = UUID.randomUUID();

        var user = new User(
                sampleId,
                "Jenner",
                "Durand",
                LocalDate.of(1991, 1, 1),
                "Calle 123 # 45-67",
                "jenner@crediya.com",
                "Jenner123*/",
                "12345678",
                "98765432",
                Roles.CLIENT.getId(),
                new BigDecimal("3000000")
        );

        var responseDTO = new UserDTO(
                sampleId.toString(),
                "Jenner",
                "Durand",
                "1991-01-01",
                "Calle 123 # 45-67",
                "jenner@crediya.com",
                "12345678",
                "98765432",
                Roles.CLIENT.getId().toString(),
                new BigDecimal("3000000")
        );

        when(getUserByIdentityDocumentUseCase.execute(identityDocument)).thenReturn(Mono.just(user));
        when(userDTOMapper.toUserDTOFromModel(user)).thenReturn(responseDTO);

        webTestClient.get()
                .uri("/api/v1/usuarios/identity-document/{identityDocument}", identityDocument)
                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .value(userResponse -> {
                    Assertions
                            .assertThat(userResponse.email())
                            .isEqualTo("jenner@crediya.com");
                });
    }

    @Test
    void testRegisterUserValidationError() {
        var dto = new RegisterUserDTO(
                "Jenner",
                "Durand",
                "not-a-date",
                null,
                "jenner@crediya.com",
                "Jenner123*/",
                "12345678",
                "98765432",
                new BigDecimal("3000000"),
                null
        );

        when(dtoValidator.validate(dto))
                .thenReturn(Mono.error(new ConstraintViolationException(Set.of())));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(422);
    }

    @Test
    void testListUsersByIdentityDocumentsSuccess() {
        var dto = new SearchListUsersByIdentityDocumentsDTO(
                List.of("12345678", "87654321")
        );

        var user1 = new User(
                UUID.randomUUID(),
                "Jenner",
                "Durand",
                LocalDate.of(1991, 1, 1),
                "Calle 123",
                "jenner@crediya.com",
                "Jenner123*/",
                "12345678",
                "987654321",
                Roles.CLIENT.getId(),
                new BigDecimal("3000000")
        );

        var user2 = new User(
                UUID.randomUUID(),
                "Carlos",
                "Perez",
                LocalDate.of(1990, 5, 10),
                "Calle 456",
                "carlos@crediya.com",
                "Carlos123*/",
                "87654321",
                "123456789",
                Roles.ADVISOR.getId(),
                new BigDecimal("4000000")
        );

        var userDTO1 = new UserDTO(
                user1.getId().toString(),
                user1.getName(),
                user1.getLastName(),
                user1.getBirthDate().toString(),
                user1.getAddress(),
                user1.getEmail(),
                user1.getIdentityDocument(),
                user1.getTelephone(),
                user1.getRoleId().toString(),
                user1.getBaseSalary()
        );

        var userDTO2 = new UserDTO(
                user2.getId().toString(),
                user2.getName(),
                user2.getLastName(),
                user2.getBirthDate().toString(),
                user2.getAddress(),
                user2.getEmail(),
                user2.getIdentityDocument(),
                user2.getTelephone(),
                user2.getRoleId().toString(),
                user2.getBaseSalary()
        );

        when(roleValidator.validateRole(Roles.ADVISOR)).thenReturn(Mono.empty());
        when(listUsersByIdentityDocumentsUseCase.execute(dto.identityDocuments()))
                .thenReturn(Flux.just(user1, user2));
        when(userDTOMapper.toUserDTOFromModel(user1)).thenReturn(userDTO1);
        when(userDTOMapper.toUserDTOFromModel(user2)).thenReturn(userDTO2);

        webTestClient.post()
                .uri("/api/v1/usuarios/identity-documents")
                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDTO.class)
                .value(users -> {
                    Assertions.assertThat(users).hasSize(2);
                    Assertions.assertThat(users.get(0).email()).isEqualTo(user1.getEmail());
                    Assertions.assertThat(users.get(1).email()).isEqualTo(user2.getEmail());
                });
    }
}
