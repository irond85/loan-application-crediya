package co.irond.crediya.r2dbc.repository.adapter;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.dto.FilteredApplicationDto;
import co.irond.crediya.model.dto.UpdateLoanApplicationRequestDto;
import co.irond.crediya.r2dbc.entity.ApplicationEntity;
import co.irond.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.irond.crediya.r2dbc.repository.ApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
public class ApplicationRepositoryAdapter extends ReactiveAdapterOperations<
        Application,
        ApplicationEntity,
        Long,
        ApplicationRepository
        > implements co.irond.crediya.model.application.gateways.ApplicationRepository {
    public ApplicationRepositoryAdapter(ApplicationRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Application.class));
    }

    @Override
    public Mono<Application> saveApplication(Application application) {
        return save(application);
    }

    @Override
    public Flux<Application> findAllApplications() {
        return findAll();
    }

    @Override
    public Mono<List<FilteredApplicationDto>> findAllApplicationsPaging(long status, long offset, int limit) {
        return repository.findAllByPage(status, offset, limit).collectList();
    }

    @Override
    public Mono<Long> countAll(long status) {
        return repository.countAll(status);
    }

    @Override
    public Mono<Application> updateLoanApplication(UpdateLoanApplicationRequestDto updateLoanApplicationRequestDto) {
        return repository.updateStatusApplication(updateLoanApplicationRequestDto.idStatus(), updateLoanApplicationRequestDto.nroApplication());
    }

    @Override
    public Mono<Application> findApplicationById(long id) {
        return findById(id);
    }

    @Override
    public Flux<FilteredApplicationDto> getApplicationsByUserEmailAndState(String email, Long idStatus) {
        return repository.getApplicationsByUserEmailAndState(email, idStatus);
    }
}
