package co.com.crediya.api.v1;

import co.com.crediya.api.dto.user.RegisterUserDTO;
import co.com.crediya.api.dto.user.UserDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.api.presentation.contract.DTOValidator;
import co.com.crediya.api.presentation.contract.UUIDValidator;
import co.com.crediya.api.presentation.user.v1.UserRouterV1;
import co.com.crediya.api.presentation.user.v1.handler.GetUserByIdHandlerV1;
import co.com.crediya.api.presentation.user.v1.handler.RegisterUserHandlerV1;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.getuserbyid.GetUserByIdUseCase;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@ContextConfiguration(classes = {UserRouterV1.class, RegisterUserHandlerV1.class, GetUserByIdHandlerV1.class})
@WebFluxTest
class UserRouterV1Test {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private GetUserByIdUseCase getUserByIdUseCase;

    @MockitoBean
    private UserDTOMapper userDTOMapper;

    @MockitoBean
    private DTOValidator dtoValidator;

    @MockitoBean
    private UUIDValidator uuidValidator;

    @Test
    void testRegisterUserSuccess() {
        var sampleId = UUID.randomUUID();
        var dto = new RegisterUserDTO(
                "Jenner",
                "Durand",
                "1996-12-12",
                null,
                "jenner@crediya.com",
                "12345678",
                "98765432",
                new BigDecimal("3000000")
        );

        var user = new User(
                sampleId,
                "Jenner",
                "Durand",
                LocalDate.of(1996, 12, 12),
                null,
                "jenner@crediya.com",
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
        when(registerUserUseCase.execute(user)).thenReturn(Mono.just(user));
        when(userDTOMapper.toUserDTOFromModel(user)).thenReturn(responseDTO);

        webTestClient.post()
                .uri("/api/v1/user/")
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
                .uri("/api/v1/user/{id}", sampleId.toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .value(userResponse -> {
                    Assertions
                            .assertThat(userResponse.email())
                            .isEqualTo("jenner@crediya.com");
                });
    }
}
