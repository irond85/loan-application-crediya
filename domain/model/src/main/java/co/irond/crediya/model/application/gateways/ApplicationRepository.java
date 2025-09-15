package co.irond.crediya.model.application.gateways;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.dto.FilteredApplicationDto;
import co.irond.crediya.model.dto.UpdateLoanApplicationRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ApplicationRepository {

    Mono<Application> saveApplication(Application application);

    Flux<Application> findAllApplications();

    Mono<List<FilteredApplicationDto>> findAllApplicationsPaging(long status, long offset, int limit);

    Mono<Long> countAll(long status);

    Mono<Application> updateLoanApplication(UpdateLoanApplicationRequestDto updateLoanApplicationRequestDto);

    Mono<Application> findApplicationById(long id);

    Flux<FilteredApplicationDto> getApplicationsByUserEmailAndState(String email, Long idStatus);
}
