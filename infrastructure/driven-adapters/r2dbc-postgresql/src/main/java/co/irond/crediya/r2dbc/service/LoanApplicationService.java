package co.irond.crediya.r2dbc.service;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.loantype.LoanType;
import co.irond.crediya.model.status.Status;
import co.irond.crediya.r2dbc.dto.LoanApplicationResponse;
import co.irond.crediya.usecase.application.ApplicationUseCase;
import co.irond.crediya.usecase.loantype.LoanTypeUseCase;
import co.irond.crediya.usecase.status.StatusUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationService {

    private final ApplicationUseCase applicationUseCase;
    private final LoanTypeUseCase loanTypeUseCase;
    private final StatusUseCase statusUseCase;
    private final TransactionalOperator transactionalOperator;

    public Flux<LoanApplicationResponse> getAllApplications() {
        return applicationUseCase.getAllApplications()
                .flatMap(application -> {
                    Mono<LoanType> loanTypeMono = getLoanTypeById(application.getIdLoanType());
                    Mono<Status> statusMono = getStatusById(application.getIdStatus());
                    return Mono.zip(loanTypeMono, statusMono)
                            .map(tuple -> {
                                LoanType loanType = tuple.getT1();
                                Status status = tuple.getT2();
                                return LoanApplicationResponse.builder().application(application).status(status).loanType(loanType).build();
                            });
                });
    }

    public Mono<LoanType> getLoanTypeById(Long id) {
        return loanTypeUseCase.getLoanTypeById(id);
    }

    public Mono<Status> getStatusById(Long id) {
        return statusUseCase.getStatusById(id);
    }

    public Mono<Application> createApplication(Application application) {
        return transactionalOperator.execute(transaction ->
                        applicationUseCase.saveApplication(application)
                )
                .doOnNext(applicationSaved -> log.info("Application of {} saved successfully.", applicationSaved.getEmail()))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .single();
    }

}
