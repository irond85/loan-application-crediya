package co.irond.crediya.model.debtcapacity;

import co.irond.crediya.model.dto.AutomaticValidationDto;
import reactor.core.publisher.Mono;

public interface DebtCapacityGateway {
    Mono<Void> sendValidationMessage(AutomaticValidationDto validationDto);
}
