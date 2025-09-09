package co.irond.crediya.r2dbc.service;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.dto.FilteredApplicationDto;
import co.irond.crediya.model.dto.LoanApplication;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.model.exceptions.ErrorCode;
import co.irond.crediya.r2dbc.dto.PageResponse;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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

    private final String emailFromToken = "myEmail@token.com";
    private final Long allRows = 21L;
    private FilteredApplicationDto filteredApplicationDto;
    private final long status = 1L;

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

        filteredApplicationDto =
                new FilteredApplicationDto(1L, new BigDecimal("5000"), 12,
                        emailFromToken, "Sheshin",
                        "Libre inversion", new BigDecimal(2),
                        "Pendiente de revision", new BigDecimal(10000),
                        new BigDecimal(100));

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

    @Test
    void getAllApplicationsPaging() {
        List<FilteredApplicationDto> applications = List.of(filteredApplicationDto);

        when(applicationUseCase.countAll(anyLong())).thenReturn(Mono.just(allRows));
        when(applicationUseCase.getAllApplicationsPaging(anyLong(), anyLong(), anyInt())).thenReturn(Mono.just(applications));

        int page = 0;
        int size = 5;
        Mono<PageResponse<FilteredApplicationDto>> result = loanApplicationService.getAllApplicationsPaging(page, size, status);

        StepVerifier.create(result)
                .assertNext(pageResponse -> {
                    assertThat(pageResponse.totalElements()).isEqualTo(21L);
                    assertThat(pageResponse.content()).hasSize(1);
                })
                .verifyComplete();
    }

    @Test
    void countAll() {
        when(applicationUseCase.countAll(anyLong())).thenReturn(Mono.just(allRows));

        Mono<Long> result = loanApplicationService.countAll(status);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(allRows))
                .verifyComplete();
    }

}