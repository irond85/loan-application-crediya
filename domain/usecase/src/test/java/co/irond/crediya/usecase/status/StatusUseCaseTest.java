package co.irond.crediya.usecase.status;

import co.irond.crediya.model.status.Status;
import co.irond.crediya.model.status.gateways.StatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusUseCaseTest {

    @InjectMocks
    private StatusUseCase statusUseCase;

    @Mock
    private StatusRepository statusRepository;

    private Status status;

    @Test
    void getStatusById_shouldReturnSomething() {
        status = new Status();
        status.setId(1L);
        status.setName("Pendiente de revisión");
        status.setDescription("Solicitud pendiente de revisión");

        when(statusRepository.findByIdStatus(anyLong())).thenReturn(Mono.just(status));

        Mono<Status> response = statusUseCase.getStatusById(1L);

        StepVerifier.create(response)
                .expectNextMatches(value -> value.equals(status))
                .verifyComplete();

        verify(statusRepository, times(1)).findByIdStatus(anyLong());
    }
}
