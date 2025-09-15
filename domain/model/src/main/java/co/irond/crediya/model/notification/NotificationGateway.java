package co.irond.crediya.model.notification;

import co.irond.crediya.model.dto.FilteredApplicationDto;
import reactor.core.publisher.Mono;

public interface NotificationGateway {
    Mono<Void> sendNotification(FilteredApplicationDto filteredApplicationDto);
}