package co.com.crediya.api.presentation.auth.v1;

import co.com.crediya.api.presentation.auth.v1.handler.LoginUserHandlerV1;
import co.com.crediya.api.presentation.auth.v1.handler.MeUserHandlerV1;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthRouterV1 {
    @Bean("authRouterFunction")
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/login",
                    beanClass = LoginUserHandlerV1.class,
                    beanMethod = "handle",
                    method = RequestMethod.POST
            ),
            @RouterOperation(
                    path = "/api/v1/me",
                    beanClass = MeUserHandlerV1.class,
                    beanMethod = "handle",
                    method = RequestMethod.GET
            )
    })
    public RouterFunction<ServerResponse> routerFunction(
            LoginUserHandlerV1 loginUserHandlerV1,
            MeUserHandlerV1 meUserHandlerV1
    ) {
        return RouterFunctions
                .route()
                .path("/api/v1", builder ->
                        builder
                                .POST("/login", loginUserHandlerV1::handle)
                                .GET("/me", meUserHandlerV1::handle)
                ).build();
    }
}
