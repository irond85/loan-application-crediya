package co.irond.crediya.r2dbc.repository.adapter;

import co.irond.crediya.model.status.Status;
import co.irond.crediya.model.status.gateways.StatusRepository;
import co.irond.crediya.r2dbc.entity.StatusEntity;
import co.irond.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.irond.crediya.r2dbc.repository.StatusReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class StatusReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Status,
        StatusEntity,
        Long,
        StatusReactiveRepository
        > implements StatusRepository {
    public StatusReactiveRepositoryAdapter(StatusReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Status.class));
    }


    @Override
    public Mono<Status> findByIdStatus(Long id) {
        return findById(id);
    }
}
