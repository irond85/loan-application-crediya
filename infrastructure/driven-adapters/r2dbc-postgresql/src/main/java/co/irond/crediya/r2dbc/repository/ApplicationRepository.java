package co.irond.crediya.r2dbc.repository;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.dto.FilteredApplicationDto;
import co.irond.crediya.r2dbc.entity.ApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository extends ReactiveCrudRepository<ApplicationEntity, Long>, ReactiveQueryByExampleExecutor<ApplicationEntity> {

    @Query("""
             SELECT
                 a.id_application AS nro,
                 a.amount,
                 a.term,
                 a.email,
                 T.name AS type,
                 T.interest_rate AS interest,
                 s.name AS status
             FROM application a
             INNER JOIN loan_type AS T ON a.id_loan_type = T.id_loan_type
             INNER JOIN status AS s ON a.id_status = s.id_status
             WHERE a.id_status = :status
             ORDER BY a.id_application ASC
             OFFSET :offset LIMIT :limit
            """)
    Flux<FilteredApplicationDto> findAllByPage(long status, long offset, int limit);

    @Query("SELECT COUNT(*) FROM application a WHERE a.id_status = :status")
    Mono<Long> countAll(long status);

    @Query("UPDATE application SET id_status = :status WHERE id_application = :application RETURNING *")
    Mono<Application> updateStatusApplication(long status, long application);

}
