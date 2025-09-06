package co.irond.crediya.r2dbc.dto;

import java.util.List;

public record PageResponse<T>(List<T> content,
                              int pageNumber,
                              int pageSize,
                              long totalElements) {
}
