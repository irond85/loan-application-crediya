package co.irond.crediya.usecase.application;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.application.gateways.ApplicationRepository;
import co.irond.crediya.model.dto.LoanApplication;
import co.irond.crediya.model.dto.UserDto;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.model.loantype.LoanType;
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

    private LoanType loanType;
    private Application application;
    private LoanApplication loanApplication;
    private UserDto userDto;

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

        Mono<Application> response = applicationUseCase.saveApplication(loanApplication);

        StepVerifier.create(response)
                .expectNextMatches(value -> value.equals(application))
                .verifyComplete();

        verify(loanTypeUseCase, times(1)).getLoanTypeById(anyLong());
        verify(userGateway, times(1)).getUserByDni(anyString());
        verify(applicationRepository, times(1)).saveApplication(any(Application.class));
    }

    @Test
    void saveApplication_shouldReturnException() {
        application.setIdLoanType(2L);

        when(loanTypeUseCase.getLoanTypeById(anyLong())).thenThrow(new CrediYaException(ErrorCode.INVALID_LOAN_TYPE));

        Executable executable = () -> applicationUseCase.saveApplication(loanApplication);

        CrediYaException exception = assertThrows(CrediYaException.class, executable);
        assertEquals("Not exists a loan type with this id.", exception.getMessage());

        verify(loanTypeUseCase, times(1)).getLoanTypeById(anyLong());
        verify(applicationRepository, times(0)).saveApplication(any(Application.class));
    }

    @Test
    void getAllApplications_shouldReturnSomething() {
        when(applicationRepository.findAllApplications()).thenReturn(Flux.just(application));

        Flux<Application> response = applicationUseCase.getAllApplications();

        StepVerifier.create(response)
                .expectNextMatches(value -> value.equals(application))
                .verifyComplete();

        verify(applicationRepository, times(1)).findAllApplications();
    }

    @Test
    void saveApplication_shouldReturnExceptionEmail() {
        application.setEmail("correo@correo.com");

        when(loanTypeUseCase.getLoanTypeById(anyLong())).thenThrow(new CrediYaException(ErrorCode.USER_NOT_MATCH));

        Executable executable = () -> applicationUseCase.saveApplication(loanApplication);

        CrediYaException exception = assertThrows(CrediYaException.class, executable);
        assertEquals("Can't do a loan application for different user.", exception.getMessage());

        verify(loanTypeUseCase, times(1)).getLoanTypeById(anyLong());
        verify(applicationRepository, times(0)).saveApplication(any(Application.class));
    }

}
