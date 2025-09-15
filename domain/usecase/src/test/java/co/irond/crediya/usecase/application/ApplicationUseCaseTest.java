package co.irond.crediya.usecase.application;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.application.gateways.ApplicationRepository;
import co.irond.crediya.model.debtcapacity.DebtCapacityGateway;
import co.irond.crediya.model.dto.*;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.model.loantype.LoanType;
import co.irond.crediya.model.notification.NotificationGateway;
import co.irond.crediya.model.user.UserGateway;
import co.irond.crediya.usecase.loantype.LoanTypeUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationUseCaseTest {

    @InjectMocks
    private ApplicationUseCase applicationUseCase;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private LoanTypeUseCase loanTypeUseCase;

    @Mock
    private UserGateway userGateway;

    @Mock
    private NotificationGateway notificationGateway;

    @Mock
    private DebtCapacityGateway debtCapacityGateway;

    private LoanType loanType;
    private Application application;
    private LoanApplication loanApplication;
    private UserDto userDto;
    private FilteredApplicationDto filteredApplicationDto;
    private UpdateLoanApplicationRequestDto updateLoanApplicationRequestDto;
    private AutomaticValidationDto automaticValidationDto;

    private final long status = 1L;
    private String userEmail = "myEmail@mail.com";

    @BeforeEach
    void initMocks() {
        application = new Application();
        application.setId(1L);
        application.setAmount(BigDecimal.TEN);
        application.setTerm(12);
        application.setEmail(userEmail);
        application.setIdStatus(1L);
        application.setIdLoanType(1L);

        loanType = new LoanType(
                1L,
                "Libre Inversion",
                BigDecimal.ONE,
                BigDecimal.TEN,
                BigDecimal.ONE,
                true
        );

        loanApplication = new LoanApplication();
        loanApplication.setDni("12345");
        loanApplication.setIdLoanType(1L);
        loanApplication.setTerm(12);
        loanApplication.setAmount(BigDecimal.TEN);
        loanApplication.setEmailLogged(userEmail);

        userDto = new UserDto("Sheshin", "Last", null, "my address", "300", userEmail, BigDecimal.TEN, "12345", 1L);

        filteredApplicationDto =
                new FilteredApplicationDto(1L, new BigDecimal("10"), 12,
                        userEmail, "Sheshin",
                        "Libre inversion", new BigDecimal(2),
                        "Pendiente de revision", new BigDecimal(10000),
                        new BigDecimal(100));

        updateLoanApplicationRequestDto = new UpdateLoanApplicationRequestDto(1L, 4L);

        automaticValidationDto = new AutomaticValidationDto();
        automaticValidationDto.setApplicationId(1L);
        automaticValidationDto.setApplicantEmail(userEmail);
        automaticValidationDto.setApplicantSalary(BigDecimal.TEN);
        automaticValidationDto.setNewLoanAmount(BigDecimal.ONE);
        automaticValidationDto.setNewLoanInterestRate(BigDecimal.ONE);
        automaticValidationDto.setNewLoanTerm(12);
        automaticValidationDto.setActiveLoans(List.of(filteredApplicationDto));
    }

    @Test
    void saveApplication_shouldSave() {
        loanType = new LoanType(
                1L,
                "Libre Inversion",
                BigDecimal.ONE,
                BigDecimal.TEN,
                BigDecimal.ONE,
                true
        );

        when(loanTypeUseCase.getLoanTypeById(anyLong())).thenReturn(Mono.just(loanType));
        when(userGateway.getUserByDni(anyString())).thenReturn(Mono.just(userDto));
        when(applicationRepository.saveApplication(any(Application.class))).thenReturn(Mono.just(application));
        when(applicationRepository.getApplicationsByUserEmailAndState(anyString(), anyLong()))
                .thenReturn(Flux.just(filteredApplicationDto));
        when(debtCapacityGateway.sendValidationMessage(any(AutomaticValidationDto.class))).thenReturn(Mono.empty());

        Mono<Application> response = applicationUseCase.saveApplication(loanApplication);

        StepVerifier.create(response)
                .expectNextMatches(value -> value.equals(application))
                .verifyComplete();

        verify(loanTypeUseCase, times(1)).getLoanTypeById(anyLong());
        verify(userGateway, times(1)).getUserByDni(anyString());
        verify(applicationRepository, times(1)).saveApplication(any(Application.class));
        verify(applicationRepository, times(1)).getApplicationsByUserEmailAndState(anyString(), anyLong());
        verify(debtCapacityGateway, times(1)).sendValidationMessage(any(AutomaticValidationDto.class));
    }

    @Test
    void saveApplication_shouldReturnException() {
        application.setIdLoanType(2L);

        when(userGateway.getUserByDni(anyString())).thenReturn(Mono.just(userDto));
        when(loanTypeUseCase.getLoanTypeById(anyLong())).thenThrow(new CrediYaException(ErrorCode.INVALID_LOAN_TYPE));

        Executable executable = () -> applicationUseCase.saveApplication(loanApplication);

        CrediYaException exception = assertThrows(CrediYaException.class, executable);
        assertEquals("Not exists a loan type with this id.", exception.getMessage());

        verify(loanTypeUseCase, times(1)).getLoanTypeById(anyLong());
        verify(applicationRepository, times(0)).saveApplication(any(Application.class));
    }

    @Test
    void getAllApplications_shouldReturnSomething() {
        List<FilteredApplicationDto> applications = List.of(filteredApplicationDto);

        when(applicationRepository.findAllApplicationsPaging(anyLong(), anyLong(), anyInt())).thenReturn(Mono.just(applications));
        when(userGateway.getUserByEmail(anyString())).thenReturn(Mono.just(userDto));

        long offset = 0L;
        int limit = 5;
        Mono<List<FilteredApplicationDto>> response = applicationUseCase.getAllApplicationsPaging(status, offset, limit);

        StepVerifier.create(response)
                .assertNext(list -> {
                    assertThat(list).hasSize(1);
                    FilteredApplicationDto dto = list.get(0);
                    assertThat(dto.name()).isEqualTo("Sheshin");
                    assertThat(dto.baseSalary()).isEqualTo(BigDecimal.TEN);
                    assertThat(dto.monthlyRequestAmount()).isEqualTo(BigDecimal.ONE);
                })
                .verifyComplete();

        verify(applicationRepository, times(1)).findAllApplicationsPaging(anyLong(), anyLong(), anyInt());
        verify(userGateway, times(1)).getUserByEmail(anyString());
    }

    @Test
    void countAll() {
        Long allRows = 21L;
        when(applicationRepository.countAll(anyLong())).thenReturn(Mono.just(allRows));

        Mono<Long> response = applicationUseCase.countAll(status);

        StepVerifier.create(response)
                .expectNextMatches(value -> value.equals(allRows))
                .verifyComplete();
    }

    @Test
    void saveApplication_shouldReturnExceptionEmail() {
        application.setEmail("correo@correo.com");

        when(userGateway.getUserByDni(anyString())).thenReturn(Mono.just(userDto));
        when(loanTypeUseCase.getLoanTypeById(anyLong())).thenThrow(new CrediYaException(ErrorCode.USER_NOT_MATCH));

        Executable executable = () -> applicationUseCase.saveApplication(loanApplication);

        CrediYaException exception = assertThrows(CrediYaException.class, executable);
        assertEquals("Can't do a loan application for different user.", exception.getMessage());

        verify(userGateway, times(1)).getUserByDni(anyString());
        verify(loanTypeUseCase, times(1)).getLoanTypeById(anyLong());
        verify(applicationRepository, times(0)).saveApplication(any(Application.class));
    }

    @Test
    void updateLoanApplication() {
        when(applicationRepository.findApplicationById(anyLong())).thenReturn(Mono.just(application));
        when(userGateway.getUserByEmail(anyString())).thenReturn(Mono.just(userDto));
        when(loanTypeUseCase.getLoanTypeById(anyLong())).thenReturn(Mono.just(loanType));
        when(applicationRepository.updateLoanApplication(any(UpdateLoanApplicationRequestDto.class)))
                .thenReturn(Mono.just(application));
        when(notificationGateway.sendNotification(any(FilteredApplicationDto.class))).thenReturn(Mono.empty());

        Mono<FilteredApplicationDto> response = applicationUseCase.updateLoanApplication(updateLoanApplicationRequestDto);

        StepVerifier.create(response)
                .expectNextMatches(value -> value.amount().equals(application.getAmount())
                        && value.status().equalsIgnoreCase(Objects.requireNonNull(StatusEnum.getById(updateLoanApplicationRequestDto.idStatus())).getName()))
                .verifyComplete();

        verify(applicationRepository, times(1)).findApplicationById(anyLong());
        verify(userGateway, times(1)).getUserByEmail(anyString());
        verify(loanTypeUseCase, times(1)).getLoanTypeById(anyLong());
        verify(applicationRepository, times(1)).updateLoanApplication(any(UpdateLoanApplicationRequestDto.class));
        verify(notificationGateway, times(1)).sendNotification(any(FilteredApplicationDto.class));
    }

    @Test
    void updateLoanApplication_returnExceptionLoanApplication() {
        application.setIdLoanType(5L);

        when(applicationRepository.findApplicationById(anyLong())).thenThrow(new CrediYaException(ErrorCode.LOAN_APPLICATION_NOT_FOUND));

        Executable executable = () -> applicationUseCase.updateLoanApplication(updateLoanApplicationRequestDto);

        CrediYaException exception = assertThrows(CrediYaException.class, executable);
        assertEquals("The loan application with id doesn't exists.", exception.getMessage());

        verify(applicationRepository, times(1)).findApplicationById(anyLong());
        verify(applicationRepository, times(0)).updateLoanApplication(any(UpdateLoanApplicationRequestDto.class));
    }

    @Test
    void updateLoanApplication_returnExceptionStatus() {
        when(applicationRepository.findApplicationById(anyLong())).thenThrow(new CrediYaException(ErrorCode.STATUS_NOT_CHANGE));

        Executable executable = () -> applicationUseCase.updateLoanApplication(updateLoanApplicationRequestDto);

        CrediYaException exception = assertThrows(CrediYaException.class, executable);
        assertEquals("The loan application with id already has this status.", exception.getMessage());

        verify(applicationRepository, times(1)).findApplicationById(anyLong());
        verify(applicationRepository, times(0)).updateLoanApplication(any(UpdateLoanApplicationRequestDto.class));
    }
}
