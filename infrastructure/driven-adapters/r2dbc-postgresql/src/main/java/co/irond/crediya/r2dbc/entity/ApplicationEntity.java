package co.irond.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("application")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApplicationEntity {

    @Id
    @Column("id_application")
    private Long id;
    private BigDecimal amount;
    private Integer term;
    private String email;
    @Column("id_status")
    private Long idStatus;
    @Column("id_loan_type")
    private Long idLoanType;

}
