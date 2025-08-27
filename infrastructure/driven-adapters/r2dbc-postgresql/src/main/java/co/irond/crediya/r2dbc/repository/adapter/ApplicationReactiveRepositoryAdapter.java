package co.irond.crediya.r2dbc.repository.adapter;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.application.gateways.ApplicationRepository;
import co.irond.crediya.r2dbc.entity.ApplicationEntity;
import co.irond.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.irond.crediya.r2dbc.repository.ApplicationReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Application,
        ApplicationEntity,
        Long,
        ApplicationReactiveRepository
        > implements ApplicationRepository {
    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Application.class));
    }

    @Override
    @Transactional
    public Mono<Application> saveApplication(Application application) {
        return save(application);
    }

    @Override
    public Flux<Application> findAllApplications() {
        return findAll();
    }
}
