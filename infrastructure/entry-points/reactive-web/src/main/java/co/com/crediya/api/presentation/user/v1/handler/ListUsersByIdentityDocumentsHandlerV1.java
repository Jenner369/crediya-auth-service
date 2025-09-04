package co.com.crediya.api.presentation.user.v1.handler;

import co.com.crediya.api.contract.RoleValidator;
import co.com.crediya.api.contract.RouteHandler;
import co.com.crediya.api.contract.UUIDValidator;
import co.com.crediya.api.dto.common.ErrorResponseDTO;
import co.com.crediya.api.dto.user.SearchListUsersByIdentityDocumentsDTO;
import co.com.crediya.api.dto.user.UserDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.model.role.enums.Roles;
import co.com.crediya.usecase.getuserbyid.GetUserByIdUseCase;
import co.com.crediya.usecase.listusersbyidentitydocuments.ListUsersByIdentityDocumentsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListUsersByIdentityDocumentsHandlerV1 implements RouteHandler {

    private final ListUsersByIdentityDocumentsUseCase listUsersByIdentityDocumentsUseCase;
    private final UserDTOMapper userDTOMapper;
    private final RoleValidator roleValidator;

    @Operation(
            tags = {"User API"},
            summary = "Obtener listado de usuarios por documentos de identidad",
            description = "Permite obtener un listado de usuarios a partir de una lista de documentos de identidad",
            requestBody = @RequestBody(
                    description = "Lista de documentos de identidad",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SearchListUsersByIdentityDocumentsDTO.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))))
    @ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(schema = @Schema))
    @ApiResponse(responseCode = "422", description = "Error de validación",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request
                .bodyToMono(SearchListUsersByIdentityDocumentsDTO.class)
                .flatMap(dto -> roleValidator.validateRole(Roles.ADVISOR).thenReturn(dto))
                .doOnNext(user -> {
                    var rid = request.exchange().getRequest().getId();
                    log.info("[{}] POST /v1/usuarios/identity-documents - Listar usuarios por documentos de identidad", rid);
                })
                .map(SearchListUsersByIdentityDocumentsDTO::identityDocuments)
                .flatMapMany(listUsersByIdentityDocumentsUseCase::execute)
                .map(userDTOMapper::toUserDTOFromModel)
                .collectList()
                .flatMap(users -> ServerResponse.ok().bodyValue(users));
    }
}
