package co.irond.crediya.usecase.application;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.application.gateways.ApplicationRepository;
import co.irond.crediya.model.dto.FilteredApplicationDto;
import co.irond.crediya.model.dto.LoanApplication;
import co.irond.crediya.model.dto.UserDto;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.model.user.UserGateway;
import co.irond.crediya.usecase.loantype.LoanTypeUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeUseCase loanTypeUseCase;
    private final UserGateway userGateway;

    public Mono<Application> saveApplication(LoanApplication loanApplication) {

        Mono<UserDto> userEmailMono = userGateway.getUserByDni(loanApplication.getDni());
        Mono<Boolean> loanTypeMono = loanTypeUseCase.getLoanTypeById(loanApplication.getIdLoanType()).hasElement();

        return Mono.zip(userEmailMono, loanTypeMono)
                .filter(tuple -> true)
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.USER_NOT_FOUND)))
                .filter(tuple -> tuple.getT1().email().equalsIgnoreCase(loanApplication.getEmailLogged()))
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.USER_NOT_MATCH)))
                .filter(Tuple2::getT2)
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.INVALID_LOAN_TYPE)))
                .flatMap(tuple ->
                        applicationRepository.saveApplication(Application.builder()
                                .email(tuple.getT1().email())
                                .term(loanApplication.getTerm())
                                .amount(loanApplication.getAmount())
                                .idLoanType(loanApplication.getIdLoanType())
                                .idStatus(1L).build())
                );
    }

    public Flux<Application> getAllApplications() {
        return applicationRepository.findAllApplications();
    }

    public Mono<List<FilteredApplicationDto>> getAllApplicationsPaging(long status, long offset, int limit) {
        return applicationRepository.findAllApplicationsPaging(status, offset, limit);
    }

    public Mono<Long> countAll() {
        return applicationRepository.countAll();
    }

}
