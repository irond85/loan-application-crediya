package co.irond.crediya.consumer;

import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.model.user.UserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {
    private final WebClient client;

    @Override
    @CircuitBreaker(name = "getUserEmailByDni")
    public Mono<String> getUserEmailByDni(String dni) {
        return client
                .get()
                .uri("/dni/{dni}", dni)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new CrediYaException(ErrorCode.DATABASE_ERROR));
                    }
                })
                .bodyToMono(ApiResponse.class)
                .flatMap(apiResponse -> {
                    if (apiResponse.getData() != null) {
                        return Mono.just(apiResponse.getData().toString());
                    }
                    return Mono.just("");
                });

    }
}
