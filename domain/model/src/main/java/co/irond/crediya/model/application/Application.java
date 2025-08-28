package co.irond.crediya.model.application;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Application {

    private Long id;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private Long idStatus;
    private Long idLoanType;
}
