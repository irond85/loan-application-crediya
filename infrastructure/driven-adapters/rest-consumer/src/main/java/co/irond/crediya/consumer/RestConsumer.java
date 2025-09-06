package co.irond.crediya.consumer;

import co.irond.crediya.model.dto.UserDto;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.model.user.UserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {
    private final WebClient client;

    @Value("${adapter.restconsumer.v1}")
    private String pathVersion;

    @Value("${adapter.restconsumer.userByDni}")
    private String pathGetUserByDni;

    @Override
    @CircuitBreaker(name = "getUserByDni")
    public Mono<UserDto> getUserByDni(String dni) {
        return client
                .get()
                .uri(pathVersion + pathGetUserByDni, dni)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new CrediYaException(ErrorCode.DATABASE_ERROR));
                    }
                })
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserDto>>() {})
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.USER_NOT_FOUND)))
                .flatMap(apiResponse ->
                        Mono.just(apiResponse.getData())
                );
    }

    @Override
    public Mono<UserDto> getUserByEmail(String email) {
        return client
                .get()
                .uri(pathVersion + pathGetUserByDni, email)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new CrediYaException(ErrorCode.DATABASE_ERROR));
                    }
                })
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserDto>>() {})
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.USER_NOT_FOUND)))
                .flatMap(apiResponse ->
                        Mono.just(apiResponse.getData())
                );
    }
}
