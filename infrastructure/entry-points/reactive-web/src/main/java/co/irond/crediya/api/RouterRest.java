package co.irond.crediya.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/usuarios", method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listenGETUseCase"),
            @RouterOperation(path = "/api/v1/usuarios", method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "listenSaveUser")
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/v1/solicitud"), handler::listenGetAll)
                .and(route(POST("/api/v1/solicitud"), handler::listenCreateApplication));
    }
}
