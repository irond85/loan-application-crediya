package co.irond.crediya.model.status.gateways;

import co.irond.crediya.model.status.Status;
import reactor.core.publisher.Mono;

public interface StatusRepository {
    Mono<Status> findByIdStatus(Long id);
}
