package co.irond.crediya.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {

    private String dni;
    private BigDecimal amount;
    private Integer term;
    private Long idLoanType;
    private String emailLogged;

}
