package co.irond.crediya.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApplicationRequestDto {

    @NotNull(message = "nroApplication can't be null")
    @Min(value = 1, message = "nroApplication must be 1 or more")
    private Long nroApplication;
    @NotNull(message = "idStatus can't be null")
    private Long idStatus;

}
