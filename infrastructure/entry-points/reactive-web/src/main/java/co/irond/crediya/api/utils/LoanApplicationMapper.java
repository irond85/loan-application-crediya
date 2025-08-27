package co.irond.crediya.api.utils;

import co.irond.crediya.api.dto.LoanApplicationRequestDto;
import co.irond.crediya.model.application.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    @Mapping(source = "loanApplicationRequestDto.email", target = "email")
    Application toApplication(LoanApplicationRequestDto loanApplicationRequestDto);

    LoanApplicationRequestDto toLoanApplicationRequestDto(Application user);
}