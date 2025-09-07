package co.irond.crediya.security.repository;

import co.irond.crediya.security.jwt.JwtAuthManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Getter
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthManager jwtAuthManager;

    private String userToken;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        userToken = null;
        userToken = exchange.getAttribute("token");
        return jwtAuthManager.authenticate(new UsernamePasswordAuthenticationToken(userToken, userToken))
                .map(SecurityContextImpl::new);
    }
}
