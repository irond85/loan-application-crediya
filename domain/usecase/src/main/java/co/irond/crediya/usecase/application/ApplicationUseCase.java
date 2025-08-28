package co.irond.crediya.usecase.application;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.application.gateways.ApplicationRepository;
import co.irond.crediya.model.dto.LoanApplication;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.model.user.UserGateway;
import co.irond.crediya.usecase.loantype.LoanTypeUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeUseCase loanTypeUseCase;
    private final UserGateway userGateway;

    public Mono<Application> saveApplication(LoanApplication loanApplication) {

        Mono<String> userEmailMono = userGateway.getUserEmailByDni(loanApplication.getDni());
        Mono<Boolean> loanTypeMono = loanTypeUseCase.getLoanTypeById(loanApplication.getIdLoanType()).hasElement();

        return Mono.zip(userEmailMono, loanTypeMono)
                .flatMap(result -> {
                    String userEmail = result.getT1();
                    boolean loanType = result.getT2();
                    if (userEmail.isBlank()) {
                        return Mono.error(new CrediYaException(ErrorCode.USER_NOT_FOUND));
                    }
                    if (!loanType) {
                        return Mono.error(new CrediYaException(ErrorCode.INVALID_LOAN_TYPE));
                    }

                    Application application = Application.builder()
                            .email(userEmail)
                            .term(loanApplication.getTerm())
                            .amount(loanApplication.getAmount())
                            .idLoanType(loanApplication.getIdLoanType())
                            .idStatus(1L).build();
                    return applicationRepository.saveApplication(application);
                });
    }

    public Flux<Application> getAllApplications() {
        return applicationRepository.findAllApplications();
    }



}
