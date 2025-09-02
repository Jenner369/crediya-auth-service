package co.com.crediya.api.presentation.auth.v1.handler;

import co.com.crediya.api.authentication.AuthUserDetails;
import co.com.crediya.api.contract.RouteHandler;
import co.com.crediya.api.dto.user.MinimalUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class MeUserHandlerV1 implements RouteHandler {
    @Override
    @Operation(
            tags = {"Auth API"},
            summary = "Obtener información del usuario autenticado",
            description = "Permite obtener la información básica del usuario actualmente autenticado"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Información del usuario obtenida correctamente",
            content = @Content(schema = @Schema(implementation = MinimalUserDTO.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content(schema = @Schema())
    )
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(AuthUserDetails.class)
                .flatMap(userDetails -> ServerResponse
                        .ok()
                        .bodyValue(new MinimalUserDTO(
                                userDetails.getId(),
                                userDetails.getUsername(),
                                userDetails.getRoleId()
                        )));
    }
}
