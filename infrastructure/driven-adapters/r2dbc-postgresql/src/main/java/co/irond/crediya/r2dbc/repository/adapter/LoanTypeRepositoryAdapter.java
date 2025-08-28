package co.irond.crediya.r2dbc.repository.adapter;

import co.irond.crediya.model.loantype.LoanType;
import co.irond.crediya.r2dbc.entity.LoanTypeEntity;
import co.irond.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.irond.crediya.r2dbc.repository.LoanTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class LoanTypeRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType,
        LoanTypeEntity,
        Long,
        LoanTypeRepository
        > implements co.irond.crediya.model.loantype.gateways.LoanTypeRepository {
    public LoanTypeRepositoryAdapter(LoanTypeRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, LoanType.class));
    }

    @Override
    public Mono<LoanType> findByIdLoanType(Long id) {
        return findById(id);
    }

}
