package co.irond.crediya.r2dbc;

import co.irond.crediya.model.status.Status;
import co.irond.crediya.r2dbc.entity.StatusEntity;
import co.irond.crediya.r2dbc.repository.StatusRepository;
import co.irond.crediya.r2dbc.repository.adapter.StatusRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusRepositoryAdapterTest {

    @InjectMocks
    StatusRepositoryAdapter statusRepositoryAdapter;

    @Mock
    StatusRepository repository;

    @Mock
    ObjectMapper mapper;

    private StatusEntity statusEntity;
    private Status status;

    @BeforeEach
    void initMocks() {
        statusEntity = new StatusEntity();
        statusEntity.setId(1L);
        statusEntity.setName("Pendiente de revisión");
        statusEntity.setDescription("En proceso de revisión por asesor");

        status = new Status();
        status.setId(1L);
        status.setName("Pendiente de revisión");
        status.setDescription("En proceso de revisión por asesor");
    }

    @Test
    void mustFindValueById() {
        when(repository.findById(1L)).thenReturn(Mono.just(statusEntity));
        when(mapper.map(statusEntity, Status.class)).thenReturn(status);

        Mono<Status> result = statusRepositoryAdapter.findByIdStatus(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(status))
                .verifyComplete();
    }

}
