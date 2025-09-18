package co.irond.crediya.model.reports;

import co.irond.crediya.model.dto.ReportRequestDto;
import reactor.core.publisher.Mono;

public interface ReportGateway {
    Mono<Void> send(ReportRequestDto reportRequestDto);
}
