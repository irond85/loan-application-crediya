package co.irond.crediya.usecase.application;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.application.gateways.ApplicationRepository;
import co.irond.crediya.model.debtcapacity.DebtCapacityGateway;
import co.irond.crediya.model.dto.*;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.model.loantype.LoanType;
import co.irond.crediya.model.notification.NotificationGateway;
import co.irond.crediya.model.reports.ReportGateway;
import co.irond.crediya.model.user.UserGateway;
import co.irond.crediya.usecase.loantype.LoanTypeUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeUseCase loanTypeUseCase;
    private final UserGateway userGateway;
    private final NotificationGateway notificationGateway;
    private final DebtCapacityGateway debtCapacityGateway;
    private final ReportGateway reportGateway;

    public Mono<Application> saveApplication(LoanApplication loanApplicationDTO) {
        Mono<UserDto> userMono = userGateway.getUserByDni(loanApplicationDTO.getDni())
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.USER_NOT_FOUND)));

        Mono<LoanType> loanTypeMono = loanTypeUseCase.getLoanTypeById(loanApplicationDTO.getIdLoanType())
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.INVALID_LOAN_TYPE)));

        return Mono.zip(userMono, loanTypeMono)
                .filter(tuple -> tuple.getT1().email().equalsIgnoreCase(loanApplicationDTO.getEmailLogged()))
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.USER_NOT_MATCH)))
                .flatMap(tuple -> {
                    UserDto user = tuple.getT1();
                    LoanType loanType = tuple.getT2();

                    Application newApplication = Application.builder()
                            .email(user.email())
                            .term(loanApplicationDTO.getTerm())
                            .amount(loanApplicationDTO.getAmount())
                            .idLoanType(loanApplicationDTO.getIdLoanType())
                            .idStatus(1L).build();

                    if (Boolean.TRUE.equals(loanType.getAutoValid())) {
                        return applicationRepository.getApplicationsByUserEmailAndState(user.email(), StatusEnum.APPROVED.getId())
                                .collectList()
                                .flatMap(activeApplications -> {
                                    AutomaticValidationDto validationDto = AutomaticValidationDto.builder()
                                            .newLoanAmount(newApplication.getAmount())
                                            .newLoanTerm(newApplication.getTerm())
                                            .newLoanInterestRate(loanType.getInterestRate())
                                            .applicantEmail(newApplication.getEmail())
                                            .applicantSalary(user.baseSalary())
                                            .activeLoans(activeApplications)
                                            .build();

                                    return applicationRepository.saveApplication(newApplication)
                                            .flatMap(savedApplication -> {
                                                validationDto.setApplicationId(savedApplication.getId());
                                                return debtCapacityGateway.sendValidationMessage(validationDto)
                                                        .thenReturn(savedApplication);
                                            });
                                });
                    } else {
                        return applicationRepository.saveApplication(newApplication);
                    }
                });
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
                                                application.id(),
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

    public Mono<FilteredApplicationDto> updateLoanApplication(UpdateLoanApplicationRequestDto updateLoanApplicationRequestDto) {
        return applicationRepository.findApplicationById(updateLoanApplicationRequestDto.nroApplication())
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.LOAN_APPLICATION_NOT_FOUND)))
                .filter(application -> application.getIdStatus().compareTo(updateLoanApplicationRequestDto.idStatus()) != 0)
                .switchIfEmpty(Mono.error(new CrediYaException(ErrorCode.STATUS_NOT_CHANGE)))
                .flatMap(application -> {

                    Mono<UserDto> userDtoMono = userGateway.getUserByEmail(application.getEmail());
                    Mono<LoanType> loanTypeMono = loanTypeUseCase.getLoanTypeById(application.getIdLoanType());
                    Mono<Application> applicationMono = applicationRepository.updateLoanApplication(updateLoanApplicationRequestDto);

                    return createUpdatedResponse(applicationMono, userDtoMono, loanTypeMono, updateLoanApplicationRequestDto.idStatus());
                });
    }

    private Mono<FilteredApplicationDto> createUpdatedResponse(Mono<Application> applicationMono, Mono<UserDto> userDtoMono, Mono<LoanType> loanTypeMono, long status) {
        return Mono.zip(applicationMono, userDtoMono, loanTypeMono)
                .flatMap(tupleData -> {
                    Application applicationUpdated = tupleData.getT1();
                    UserDto user = tupleData.getT2();
                    LoanType loanType = tupleData.getT3();
                    String estado = StatusEnum.getById(status).getName();

                    FilteredApplicationDto filteredDto = new FilteredApplicationDto(
                            applicationUpdated.getId(),
                            applicationUpdated.getAmount(),
                            applicationUpdated.getTerm(),
                            applicationUpdated.getEmail(),
                            user.name(),
                            loanType.getName(),
                            loanType.getInterestRate(),
                            estado,
                            user.baseSalary(),
                            applicationUpdated.getAmount()
                                    .multiply(loanType.getInterestRate())
                                    .divide(new BigDecimal(applicationUpdated.getTerm()), RoundingMode.FLOOR)
                    );

                    if (estado.equalsIgnoreCase(StatusEnum.APPROVED.getName()) || estado.equalsIgnoreCase(StatusEnum.REJECTED.getName())) {
                        notificationGateway.sendNotification(filteredDto).subscribe();
                        if (estado.equalsIgnoreCase(StatusEnum.APPROVED.getName())) {
                            reportGateway.send(new ReportRequestDto("loanApplicationsApproved", applicationUpdated.getAmount())).subscribe();
                        }
                    }

                    return Mono.just(filteredDto);
                });
    }

}
