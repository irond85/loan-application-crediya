package co.irond.crediya.r2dbc;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.r2dbc.entity.ApplicationEntity;
import co.irond.crediya.r2dbc.repository.ApplicationReactiveRepository;
import co.irond.crediya.r2dbc.repository.adapter.ApplicationReactiveRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationReactiveRepositoryAdapterTest {

    @InjectMocks
    ApplicationReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    ApplicationReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private ApplicationEntity applicationEntity;
    private Application application;

    @BeforeEach
    void initMocks() {
        applicationEntity = new ApplicationEntity();
        applicationEntity.setId(1L);
        applicationEntity.setAmount(BigDecimal.TEN);
        applicationEntity.setTerm(12);
        applicationEntity.setEmail("myEmail@email.com");
        applicationEntity.setIdStatus(1L);
        applicationEntity.setIdLoanType(1L);

        application = new Application();
        application.setId(1L);
        application.setAmount(BigDecimal.TEN);
        application.setTerm(12);
        application.setEmail("myEmail@email.com");
        application.setIdStatus(1L);
        application.setIdLoanType(1L);
    }

    @Test
    void mustFindValueById() {

        when(repository.findById(1L)).thenReturn(Mono.just(applicationEntity));
        when(mapper.map(applicationEntity, Application.class)).thenReturn(application);

        Mono<Application> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(application))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll()).thenReturn(Flux.just(applicationEntity));
        when(mapper.map(applicationEntity, Application.class)).thenReturn(application);

        Flux<Application> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(application))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(mapper.map(any(Application.class), eq(ApplicationEntity.class))).thenReturn(applicationEntity);
        when(repository.save(any(ApplicationEntity.class))).thenReturn(Mono.just(applicationEntity));
        when(mapper.map(applicationEntity, Application.class)).thenReturn(application);

        Mono<Application> result = repositoryAdapter.save(application);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(application))
                .verifyComplete();
    }
}
