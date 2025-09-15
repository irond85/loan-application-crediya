package co.irond.crediya.sqs.listener;

import co.irond.crediya.model.dto.UpdateLoanApplicationRequestDto;
import co.irond.crediya.model.exceptions.CrediYaException;
import co.irond.crediya.sqs.listener.dto.SQSMessageDto;
import co.irond.crediya.usecase.application.ApplicationUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ApplicationUseCase applicationUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), SQSMessageDto.class))
                .flatMap(dto -> {
                    UpdateLoanApplicationRequestDto updateDto = new UpdateLoanApplicationRequestDto(dto.getApplicationId(), getStatusId(dto.getStatus()));

                    return applicationUseCase.updateLoanApplication(updateDto)
                            .onErrorResume(CrediYaException.class, e ->
                                    Mono.empty()
                            );
                })
                .then();
    }

    private Long getStatusId(String statusName) {
        return switch (statusName) {
            case "APROBADO" -> 4L;
            case "RECHAZADO" -> 2L;
            case "REVISION MANUAL" -> 3L;
            default -> throw new IllegalArgumentException("Estado desconocido: " + statusName);
        };
    }
}
