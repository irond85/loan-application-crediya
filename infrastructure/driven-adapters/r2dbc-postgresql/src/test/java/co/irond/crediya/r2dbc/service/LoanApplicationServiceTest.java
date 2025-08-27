package co.irond.crediya.r2dbc.service;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
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

    private Application application;

    @BeforeEach
    void initMocks() {
        application = new Application();
        application.setId(1L);
        application.setTerm(1);
        application.setAmount(new BigDecimal("4500000"));
        application.setIdStatus(1L);
        application.setIdLoanType(1L);
    }

    @Test
    void saveApplication_shouldSaveUserSuccessfully() {
        when(applicationUseCase.saveApplication(any(Application.class))).thenReturn(Mono.just(application));

        when(transactionalOperator.execute(any(TransactionCallback.class)))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return ((Mono<Application>) callback.doInTransaction(null)).flux();
                });

        Mono<Application> result = loanApplicationService.createApplication(application);

        StepVerifier.create(result)
                .expectNext(application)
                .verifyComplete();

        verify(applicationUseCase).saveApplication(application);
        verify(transactionalOperator).execute(any());
    }

    @Test
    void createLoanApplication_shouldHandleError() {
        CrediYaException exception = new CrediYaException(ErrorCode.DATABASE_ERROR);

        when(applicationUseCase.saveApplication(any(Application.class))).thenReturn(Mono.error(exception));

        when(transactionalOperator.execute(any(TransactionCallback.class)))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return ((Mono<Application>) callback.doInTransaction(null)).flux();
                });

        Mono<Application> result = loanApplicationService.createApplication(application);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CrediYaException &&
                        throwable.getMessage().equals("An error has occurred while communicating with the database."))
                .verify();

        verify(applicationUseCase).saveApplication(application);
        verify(transactionalOperator).execute(any());
    }

}