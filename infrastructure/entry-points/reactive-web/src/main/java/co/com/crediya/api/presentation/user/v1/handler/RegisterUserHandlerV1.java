package co.com.crediya.api.presentation.user.v1.handler;

import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.dto.user.RegisterUserDTO;
import co.com.crediya.api.dto.user.UserDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.api.presentation.contract.DTOValidator;
import co.com.crediya.api.presentation.contract.RouteHandler;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterUserHandlerV1 implements RouteHandler {
    private final UserDTOMapper userDTOMapper;
    private final RegisterUserUseCase registerUserUseCase;
    private final DTOValidator dtoValidator;

    @Operation(
            tags = {"User API"},
            summary = "Registrar un usuario",
            description = "Permite registrar un usuario en la plataforma",
            requestBody = @RequestBody(
                    description = "Datos del usuario a registrar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterUserDTO.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(responseCode = "400", description = "Error de dominio",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "422", description = "Error de validación",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(RegisterUserDTO.class)
                .doOnNext(user -> {
                    var rid = serverRequest.exchange().getRequest().getId();
                    log.info("[{}] POST /api/v1/usuarios - Intento de registro", rid);
                })
                .flatMap(dtoValidator::validate)
                .map(userDTOMapper::toModelFromRegisterDTO)
                .flatMap(registerUserUseCase::execute)
                .doOnNext(user -> {
                    var rid = serverRequest.exchange().getRequest().getId();
                    log.info("[{}] POST /api/v1/usuarios - Registro exitoso para el usuario con ID: {}",
                            rid,
                            user.getId()
                    );
                })
                .map(userDTOMapper::toUserDTOFromModel)
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }
}
