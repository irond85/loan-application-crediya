package co.irond.crediya.api;

import co.irond.crediya.api.config.LoanApplicationPath;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final LoanApplicationPath loanApplicationPath;

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/solicitud", method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "listenCreateLoanApplication"),
            @RouterOperation(path = "/api/v1/solicitud", method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listenGetAllByStatus"),
            @RouterOperation(path = "/api/v1/solicitud", method = RequestMethod.PUT, beanClass = Handler.class, beanMethod = "listenUpdateLoanApplication")
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(loanApplicationPath.getSolicitud()), handler::listenGetAllByStatus)
                .and(route(POST(loanApplicationPath.getSolicitud()), handler::listenCreateLoanApplication))
                .and(route(PUT(loanApplicationPath.getSolicitud()), handler::listenUpdateLoanApplication));
    }
}
