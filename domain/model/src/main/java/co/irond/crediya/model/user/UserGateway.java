package co.irond.crediya.model.user;

import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<String> getUserEmailByDni(String dni);
}
