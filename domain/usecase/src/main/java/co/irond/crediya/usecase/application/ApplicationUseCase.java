package co.irond.crediya.usecase.application;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.application.gateways.ApplicationRepository;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.model.loantype.gateways.LoanTypeRepository;
import co.irond.crediya.model.status.gateways.StatusRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;

    public Mono<Application> saveApplication(Application application) {
        return loanTypeRepository.existsById(application.getIdLoanType())
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return Mono.error(new CrediYaException(ErrorCode.INVALID_LOAN_TYPE));
                    }
                    application.setIdStatus(1L);
                    return applicationRepository.saveApplication(application);
                });
    }

    public Flux<Application> getAllApplications() {
        return applicationRepository.findAllApplications();
    }

}
