package co.com.crediya.api.presentation.user.v1;

import co.com.crediya.api.presentation.user.v1.handler.GetUserByIdHandlerV1;
import co.com.crediya.api.presentation.user.v1.handler.GetUserByIdentityDocumentHandlerV1;
import co.com.crediya.api.presentation.user.v1.handler.RegisterUserHandlerV1;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class UserRouterV1 {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    beanClass = RegisterUserHandlerV1.class,
                    beanMethod = "handle",
                    method = RequestMethod.POST
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/{id}",
                    beanClass = GetUserByIdHandlerV1.class,
                    beanMethod = "handle",
                    method = RequestMethod.GET
            )
    })
    public RouterFunction<ServerResponse> routerFunction(
            RegisterUserHandlerV1 registerUserHandlerV1,
            GetUserByIdHandlerV1 getUserByIdHandlerV1,
            GetUserByIdentityDocumentHandlerV1 getUserByIdentityDocumentHandlerV1
    ) {
        return RouterFunctions
                .route()
                .path("/api/v1/usuarios", builder ->
                        builder
                                .POST("", registerUserHandlerV1::handle)
                                .GET("identity-document/{identityDocument}", getUserByIdentityDocumentHandlerV1::handle)
                                .GET("/{id}", getUserByIdHandlerV1::handle)
                ).build();
    }
}
