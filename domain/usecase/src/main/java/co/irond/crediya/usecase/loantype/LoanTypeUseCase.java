package co.irond.crediya.usecase.loantype;

import co.irond.crediya.model.loantype.LoanType;
import co.irond.crediya.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanTypeUseCase {
    private final LoanTypeRepository loanTypeRepository;

    public Mono<LoanType> getLoanTypeById(Long id) {
        return loanTypeRepository.findByIdLoanType(id);
    }
}
