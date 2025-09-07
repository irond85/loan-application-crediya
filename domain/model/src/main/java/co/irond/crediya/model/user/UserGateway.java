package co.irond.crediya.model.user;

import co.irond.crediya.model.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<UserDto> getUserByDni(String dni);
    Mono<UserDto> getUserByEmail(String email);
}
