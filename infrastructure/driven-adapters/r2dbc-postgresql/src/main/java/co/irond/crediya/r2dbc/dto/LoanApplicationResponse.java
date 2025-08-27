package co.irond.crediya.r2dbc.dto;

import co.irond.crediya.model.application.Application;
import co.irond.crediya.model.loantype.LoanType;
import co.irond.crediya.model.status.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationResponse {

    private Application application;
    private LoanType loanType;
    private Status status;

}
