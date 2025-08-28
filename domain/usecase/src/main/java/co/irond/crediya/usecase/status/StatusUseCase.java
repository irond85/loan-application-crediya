package co.irond.crediya.usecase.status;

import co.irond.crediya.model.status.Status;
import co.irond.crediya.model.status.gateways.StatusRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class StatusUseCase {

    private final StatusRepository statusRepository;

    public Mono<Status> getStatusById(Long id) {
        return statusRepository.findByIdStatus(id);
    }

}
