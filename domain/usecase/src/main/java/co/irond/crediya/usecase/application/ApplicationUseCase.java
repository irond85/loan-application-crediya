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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        Mono<List<FilteredApplicationDto>> listApplicationsMono = applicationRepository.findAllApplicationsPaging(status, offset, limit);

        return listApplicationsMono.flatMap(applications -> {
            return Flux.fromIterable(applications)
                    .flatMap(application -> {
                        return userGateway.getUserByEmail(application.email())
                                .map(user ->
                                        new FilteredApplicationDto(
                                                application.amount(),
                                                application.term(),
                                                application.email(),
                                                user.name(),
                                                application.type(),
                                                application.interest(),
                                                application.status(),
                                                user.baseSalary(),
                                                application.amount()
                                                        .multiply(application.interest())
                                                        .divide(new BigDecimal(application.term()), RoundingMode.FLOOR)
                                        )
                                );
                    })
                    .collectList();
        });

    }

    public Mono<Long> countAll(long status) {
        return applicationRepository.countAll(status);
    }

}
