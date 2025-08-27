package co.irond.crediya.r2dbc;

import co.irond.crediya.model.loantype.LoanType;
import co.irond.crediya.r2dbc.entity.LoanTypeEntity;
import co.irond.crediya.r2dbc.repository.LoanTypeRepository;
import co.irond.crediya.r2dbc.repository.adapter.LoanTypeRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeRepositoryAdapterTest {

    @InjectMocks
    LoanTypeRepositoryAdapter loanTypeRepositoryAdapter;

    @Mock
    LoanTypeRepository repository;

    @Mock
    ObjectMapper mapper;

    private LoanTypeEntity loanTypeEntity;
    private LoanType loanType;

    @BeforeEach
    void initMocks() {
        loanTypeEntity = new LoanTypeEntity();
        loanTypeEntity.setId(1L);
        loanTypeEntity.setMinAmount(BigDecimal.ONE);
        loanTypeEntity.setMaxAmount(new BigDecimal("15000000"));
        loanTypeEntity.setName("Libre inversion");
        loanTypeEntity.setInterestRate(new BigDecimal("1.7"));
        loanTypeEntity.setAutoValid(true);

        loanType = new LoanType();
        loanType.setId(1L);
        loanType.setMinAmount(BigDecimal.ONE);
        loanType.setMaxAmount(new BigDecimal("15000000"));
        loanType.setName("Libre inversion");
        loanType.setInterestRate(new BigDecimal("1.7"));
        loanType.setAutomaticValidation(true);
    }

    @Test
    void mustFindValueById() {
        when(repository.findById(1L)).thenReturn(Mono.just(loanTypeEntity));
        when(mapper.map(loanTypeEntity, LoanType.class)).thenReturn(loanType);

        Mono<LoanType> result = loanTypeRepositoryAdapter.findByIdLoanType(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(loanType))
                .verifyComplete();
    }

}
