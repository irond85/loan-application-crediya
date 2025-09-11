package co.irond.crediya.api.utils;

import co.irond.crediya.api.dto.LoanApplicationRequestDto;
import co.irond.crediya.api.dto.UpdateApplicationRequestDto;
import co.irond.crediya.model.dto.LoanApplication;
import co.irond.crediya.model.dto.UpdateLoanApplicationRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    @Mapping(source = "loanApplicationRequestDto.dni", target = "dni")
    LoanApplication toLoanApplication(LoanApplicationRequestDto loanApplicationRequestDto);

    LoanApplicationRequestDto toLoanApplicationRequestDto(LoanApplication user);

    UpdateLoanApplicationRequestDto toUpdateLoanApplicationRequestDto(UpdateApplicationRequestDto updateRequest);
}