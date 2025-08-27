package co.irond.crediya.usecase.application;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.application.gateways.ApplicationRepository;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.usecase.loantype.LoanTypeUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeUseCase loanTypeUseCase;

    public Mono<Application> saveApplication(Application application) {
        return loanTypeUseCase.getLoanTypeById(application.getIdLoanType())
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.INVALID_LOAN_TYPE)))
                .flatMap(loanType -> {
                    application.setIdStatus(1L);
                    return applicationRepository.saveApplication(application);
                });
    }

    public Flux<Application> getAllApplications() {
        return applicationRepository.findAllApplications();
    }

}
