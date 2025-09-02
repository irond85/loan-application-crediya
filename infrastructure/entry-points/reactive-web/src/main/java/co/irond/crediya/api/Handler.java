package co.irond.crediya.api;

import co.irond.crediya.api.dto.ApiResponseDto;
import co.irond.crediya.api.dto.LoanApplicationRequestDto;
import co.irond.crediya.api.utils.LoanApplicationMapper;
import co.irond.crediya.api.utils.ValidationService;
import co.irond.crediya.constanst.OperationsMessage;
import co.irond.crediya.r2dbc.dto.LoanApplicationResponse;
import co.irond.crediya.r2dbc.service.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final LoanApplicationService loanApplicationService;
    private final ValidationService validationService;
    private final LoanApplicationMapper loanApplicationMapper;

    @Operation(
            operationId = "getAllApplications",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "get all applications successfully.",
                            content = @Content(
                                    schema = @Schema(implementation = LoanApplicationResponse.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> listenGetAll(ServerRequest serverRequest) {
        return ServerResponse.ok().body(loanApplicationService.getAllApplications(), LoanApplicationResponse.class);
    }

    @Operation(
            operationId = "createLoanApplication",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "successful operation",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Fields empty or null",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class)
                            )
                    ),
            },
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = LoanApplicationRequestDto.class)
                    )
            )
    )
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public Mono<ServerResponse> listenCreateLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationRequestDto.class)
                .doOnNext(request -> log.info(OperationsMessage.REQUEST_RECEIVED.getMessage(), request.toString()))
                .flatMap(validationService::validateObject)
                .map(loanApplicationMapper::toLoanApplication)
                .flatMap(loanApplicationService::createApplication)
                .flatMap(savedApplication -> {
                    ApiResponseDto<Object> response = ApiResponseDto.builder()
                            .status("201")
                            .message(OperationsMessage.RESOURCE_CREATED.getMessage())
                            .data(savedApplication).build();
                    return ServerResponse.status(201).contentType(MediaType.APPLICATION_JSON).bodyValue(response);
                });
    }
}
