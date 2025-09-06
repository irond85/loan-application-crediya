package co.irond.crediya.r2dbc.repository;

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
                 a.amount,
                 a.term,
                 a.email,
                 t.name AS loanType,
                 t.interest_rate AS interestRate,
                 s.name AS status
             FROM application a
             LEFT JOIN loan_type t ON a.id_loan_type = t.id_loan_type
             LEFT JOIN status s ON a.id_status = s.id_status
             WHERE a.id_status = :status
             LIMIT :limit OFFSET :offset
            """)
    Flux<FilteredApplicationDto> findAllByPage(long status, long offset, int limit);

    @Query("SELECT COUNT(*) FROM application")
    Mono<Long> countAll();

}
