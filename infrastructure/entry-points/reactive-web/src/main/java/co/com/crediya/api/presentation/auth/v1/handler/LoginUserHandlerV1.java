package co.com.crediya.api.presentation.auth.v1.handler;

import co.com.crediya.api.contract.TokenProvider;
import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.dto.user.LoginUserDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.api.contract.RouteHandler;
import co.com.crediya.usecase.loginuser.LoginUserUseCase;
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
public class LoginUserHandlerV1 implements RouteHandler {

    private final LoginUserUseCase loginUserUseCase;
    private final TokenProvider tokenProvider;
    private final UserDTOMapper mapper;

    @Override
    @Operation(
            tags = {"Auth API"},
            summary = "Login de usuario",
            description = "Permite a un usuario iniciar sesión en la plataforma",
            requestBody =
                @RequestBody(
                    description = "Datos del usuario para iniciar sesión",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginUserDTO.class))
                )
    )
    @ApiResponse(responseCode = "200", description = "Usuario autenticado correctamente",
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Error de validación",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(LoginUserDTO.class)
                .map(mapper::toLoginInputFromLoginDTO)
                .flatMap(loginUserUseCase::execute)
                .flatMap(user -> tokenProvider.generateToken(
                        user.getId().toString(),
                        user.getEmail(),
                        user.getRoleId().toString()
                ))
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }
}
