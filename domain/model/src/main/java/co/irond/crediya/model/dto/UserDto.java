package co.irond.crediya.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserDto(String name,
                      String lastName,
                      LocalDateTime birthday,
                      String address,
                      String phone,
                      String email,
                      BigDecimal baseSalary,
                      String dni,
                      Long role) {
}
