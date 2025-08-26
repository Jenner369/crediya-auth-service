package co.com.crediya.api.presentation.user.v1.handler;

import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.dto.user.UserDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.api.presentation.contract.RouteHandler;
import co.com.crediya.api.presentation.contract.UUIDValidator;
import co.com.crediya.usecase.getuserbyid.GetUserByIdUseCase;
import co.com.crediya.usecase.getuserbyidentitydocument.GetUserByIdentityDocumentUseCase;
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
public class GetUserByIdentityDocumentHandlerV1 implements RouteHandler {

    private final GetUserByIdentityDocumentUseCase getUserByIdentityDocumentUseCase;
    private final UserDTOMapper userDTOMapper;

    @Operation(
            tags = {"User API"},
            summary = "Obtener usuario por documento de identidad",
            description = "Permite obtener la información de un usuario a partir de su documento de identidad",
            parameters = {
                    @Parameter(
                            name = "identityDocument",
                            in = ParameterIn.PATH,
                            description = "Documento de identidad del usuario",
                            required = true,
                            example = "87978659"
                    )
            }
    )
    @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        return Mono.fromCallable(serverRequest.pathVariable("identityDocument")::toString)
                .flatMap(getUserByIdentityDocumentUseCase::execute)
                .map(userDTOMapper::toUserDTOFromModel)
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }
}
