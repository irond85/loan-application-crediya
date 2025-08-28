package co.irond.crediya.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ApiResponseDto<T> {

    private Integer status;
    private String message;
    private List<String> errors;
    private T data;
}
