package co.irond.crediya.model.application.gateways;

import co.irond.crediya.model.application.Application;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {

    Mono<Application> saveApplication(Application application);

    Flux<Application> findAllApplications();
}
