package co.com.crediya.api.presentation.user.v1.handler;

import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.dto.user.UserDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.api.presentation.contract.RouteHandler;
import co.com.crediya.api.presentation.contract.UUIDValidator;
import co.com.crediya.api.validation.DTOValidatorImp;
import co.com.crediya.usecase.getuserbyid.GetUserByIdUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class GetUserByIdHandlerV1 implements RouteHandler {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UserDTOMapper userDTOMapper;
    private final UUIDValidator uuidValidator;

    @Operation(
            tags = {"User API"},
            summary = "Obtener usuario por ID",
            description = "Permite obtener la información de un usuario a partir de su ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            in = ParameterIn.PATH,
                            description = "ID del usuario",
                            required = true,
                            example = "123e4567-e89b-12d3-a456-426614174000"
                    )
            }
    )
    @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(responseCode = "400", description = "Error de validación",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        return Mono.fromCallable(serverRequest.pathVariable("id")::toString)
                .flatMap(uuidValidator::validate)
                .flatMap(getUserByIdUseCase::execute)
                .map(userDTOMapper::toUserDTOFromModel)
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }
}
