package co.irond.crediya.r2dbc.service;

import co.irond.crediya.constanst.OperationsMessage;
import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.dto.FilteredApplicationDto;
import co.irond.crediya.model.dto.LoanApplication;
import co.irond.crediya.model.loantype.LoanType;
import co.irond.crediya.model.status.Status;
import co.irond.crediya.r2dbc.dto.LoanApplicationResponse;
import co.irond.crediya.r2dbc.dto.PageResponse;
import co.irond.crediya.security.jwt.JwtProvider;
import co.irond.crediya.security.repository.SecurityContextRepository;
import co.irond.crediya.usecase.application.ApplicationUseCase;
import co.irond.crediya.usecase.loantype.LoanTypeUseCase;
import co.irond.crediya.usecase.status.StatusUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationService {

    private final ApplicationUseCase applicationUseCase;
    private final LoanTypeUseCase loanTypeUseCase;
    private final StatusUseCase statusUseCase;
    private final TransactionalOperator transactionalOperator;
    private final SecurityContextRepository securityContextRepository;
    private final JwtProvider jwtProvider;

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

    public Mono<Application> createApplication(LoanApplication loanApplication) {
        String emailUserLogged = jwtProvider.getSubject(securityContextRepository.getUserToken());
        loanApplication.setEmailLogged(emailUserLogged);
        return transactionalOperator.execute(transaction ->
                        applicationUseCase.saveApplication(loanApplication)
                )
                .doOnNext(applicationSaved -> log.info(OperationsMessage.SAVE_OK.getMessage(), applicationSaved.getEmail()))
                .doOnError(throwable -> log.error(OperationsMessage.OPERATION_ERROR.getMessage(),
                        "CreateLoanApplication. " + throwable.getMessage()))
                .single();
    }

    public Mono<PageResponse<FilteredApplicationDto>> getAllApplicationsPaging(int page, int size, long status) {
        Mono<Long> totalCount = applicationUseCase.countAll(status);

        long offset = (long) (page - 1) * size;

        Mono<List<FilteredApplicationDto>> itemsMono = applicationUseCase.getAllApplicationsPaging(status, offset, size);

        return Mono.zip(itemsMono, totalCount)
                .map(tuple ->
                        new PageResponse<FilteredApplicationDto>(tuple.getT1(), page, size, tuple.getT2())
                );
    }


    public Mono<Long> countAll(long status) {
        return applicationUseCase.countAll(status);
    }

}
