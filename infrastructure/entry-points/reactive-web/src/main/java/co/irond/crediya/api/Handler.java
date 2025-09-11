package co.irond.crediya.api;

import co.irond.crediya.api.dto.ApiResponseDto;
import co.irond.crediya.api.dto.LoanApplicationRequestDto;
import co.irond.crediya.api.dto.UpdateApplicationRequestDto;
import co.irond.crediya.api.utils.LoanApplicationMapper;
import co.irond.crediya.api.utils.ValidationService;
import co.irond.crediya.constanst.OperationsMessage;
import co.irond.crediya.model.dto.FilteredApplicationDto;
import co.irond.crediya.r2dbc.dto.LoanApplicationResponse;
import co.irond.crediya.r2dbc.dto.PageResponse;
import co.irond.crediya.r2dbc.service.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
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
            operationId = "getAllApplicationsPaging",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "get all applications by status successfully.",
                            content = @Content(
                                    schema = @Schema(implementation = LoanApplicationResponse.class)
                            )
                    )
            }
    )
    @PreAuthorize("hasAuthority('ADVISOR')")
    public Mono<ServerResponse> listenGetAllByStatus(ServerRequest serverRequest) {
        int page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(1);
        int size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(5);
        int status = serverRequest.queryParam("status").map(Integer::parseInt).orElse(1);

        page = page == 0 ? 1 : page;

        Mono<PageResponse<FilteredApplicationDto>> pageMono = loanApplicationService.getAllApplicationsPaging(page, size, status);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pageMono, new ParameterizedTypeReference<PageResponse<FilteredApplicationDto>>() {
                });
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

    @Operation(
            operationId = "editLoanApplication",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "successful update operation",
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
    @PreAuthorize("hasAuthority('ADVISOR')")
    public Mono<ServerResponse> listenUpdateLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateApplicationRequestDto.class)
                .doOnNext(request -> log.info(OperationsMessage.REQUEST_RECEIVED.getMessage(), request.toString()))
                .flatMap(validationService::validateObject)
                .map(loanApplicationMapper::toUpdateLoanApplicationRequestDto)
                .flatMap(loanApplicationService::updateLoanApplication)
                .flatMap(updatedLoanApplication -> {
                    ApiResponseDto<Object> response = ApiResponseDto.builder()
                            .status("Success")
                            .message(OperationsMessage.SAVE_OK.getMessage())
                            .data(updatedLoanApplication).build();
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(response);
                });
    }
}
