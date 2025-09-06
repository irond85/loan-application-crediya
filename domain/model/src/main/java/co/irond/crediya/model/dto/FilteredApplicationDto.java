package co.irond.crediya.model.dto;

import java.math.BigDecimal;

public record FilteredApplicationDto(BigDecimal amount,
                                     int term,
                                     String email,
                                     String name,
                                     String loanType,
                                     BigDecimal interestRate,
                                     String status,
                                     BigDecimal baseSalary,
                                     BigDecimal monthlyRequestAmount) {
}
