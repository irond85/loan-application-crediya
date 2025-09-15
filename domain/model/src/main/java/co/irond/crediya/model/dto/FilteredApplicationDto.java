package co.irond.crediya.model.dto;

import java.math.BigDecimal;

public record FilteredApplicationDto(Long id, BigDecimal amount,
                                     int term,
                                     String email,
                                     String name,
                                     String type,
                                     BigDecimal interest,
                                     String status,
                                     BigDecimal baseSalary,
                                     BigDecimal monthlyRequestAmount) {
}
