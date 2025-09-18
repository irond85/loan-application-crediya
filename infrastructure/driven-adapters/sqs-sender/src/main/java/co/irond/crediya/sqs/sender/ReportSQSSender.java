package co.irond.crediya.sqs.sender;

import co.irond.crediya.model.dto.ReportRequestDto;
import co.irond.crediya.model.reports.ReportGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReportSQSSender implements ReportGateway {

    private final SQSSender sqsSender;
    private final ObjectMapper objectMapper;

    @Value("${adapter.sqs.reportQueueUrl}")
    private String reportQueueUrl;

    @Override
    public Mono<Void> send(ReportRequestDto reportRequestDto) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(reportRequestDto))
                .flatMap(message -> sqsSender.send(reportQueueUrl, message))
                .then();
    }
}
