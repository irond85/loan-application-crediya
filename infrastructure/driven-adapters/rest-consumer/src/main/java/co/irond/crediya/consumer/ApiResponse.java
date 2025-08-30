package co.irond.crediya.consumer;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApiResponse<T> {

    private String status;
    private String message;
    private List<String> errors;
    private T data;

}