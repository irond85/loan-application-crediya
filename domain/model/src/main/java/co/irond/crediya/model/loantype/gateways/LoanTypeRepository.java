package co.irond.crediya.model.loantype.gateways;

import co.irond.crediya.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {

    Mono<LoanType> findByIdLoanType(Long id);

    Mono<Boolean> existsById(Long id);
}
