package co.irond.crediya.model.dto;

import java.math.BigDecimal;

public record ReportRequestDto(String metricName, BigDecimal amountToAdd) {
}
