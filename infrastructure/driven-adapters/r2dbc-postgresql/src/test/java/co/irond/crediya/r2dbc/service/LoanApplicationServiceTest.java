package co.irond.crediya.r2dbc.service;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.dto.LoanApplication;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.security.jwt.JwtProvider;
import co.irond.crediya.security.repository.SecurityContextRepository;
import co.irond.crediya.usecase.application.ApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @InjectMocks
    LoanApplicationService loanApplicationService;

    @Mock
    TransactionalOperator transactionalOperator;

    @Mock
    ApplicationUseCase applicationUseCase;

    @Mock
    SecurityContextRepository securityContextRepository;

    @Mock
    JwtProvider jwtProvider;

    private Application application;
    private LoanApplication loanApplication;

    private String token = "Bearer token_xyz";
    private String emailFromToken = "myEmail@token.com";

    @BeforeEach
    void initMocks() {
        application = new Application();
        application.setId(1L);
        application.setTerm(12);
        application.setAmount(new BigDecimal("4500000"));
        application.setIdStatus(1L);
        application.setIdLoanType(1L);
        application.setEmail(emailFromToken);

        loanApplication = new LoanApplication();
        loanApplication.setDni("12345");
        loanApplication.setIdLoanType(1L);
        loanApplication.setTerm(12);
        loanApplication.setAmount(new BigDecimal("4500000"));

        when(securityContextRepository.getUserToken()).thenReturn(token);
        when(jwtProvider.getSubject(token)).thenReturn(emailFromToken);
    }

    @Test
    void saveApplication_shouldSaveUserSuccessfully() {
        when(applicationUseCase.saveApplication(any(LoanApplication.class))).thenReturn(Mono.just(application));

        when(transactionalOperator.execute(any(TransactionCallback.class)))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return ((Mono<Application>) callback.doInTransaction(null)).flux();
                });

        Mono<Application> result = loanApplicationService.createApplication(loanApplication);

        StepVerifier.create(result)
                .expectNext(application)
                .verifyComplete();

        verify(applicationUseCase).saveApplication(any(LoanApplication.class));
        verify(transactionalOperator).execute(any());
    }

    @Test
    void createLoanApplication_shouldHandleError() {
        CrediYaException exception = new CrediYaException(ErrorCode.DATABASE_ERROR);

        when(applicationUseCase.saveApplication(any(LoanApplication.class))).thenReturn(Mono.error(exception));

        when(transactionalOperator.execute(any(TransactionCallback.class)))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return ((Mono<Application>) callback.doInTransaction(null)).flux();
                });

        Mono<Application> result = loanApplicationService.createApplication(loanApplication);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CrediYaException &&
                        throwable.getMessage().equals("An error has occurred while communicating with the database."))
                .verify();

        verify(applicationUseCase).saveApplication(any(LoanApplication.class));
        verify(transactionalOperator).execute(any());
    }

}