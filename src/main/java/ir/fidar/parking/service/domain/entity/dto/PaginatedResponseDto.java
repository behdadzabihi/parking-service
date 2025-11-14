package ir.fidar.parking.service.domain.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Paginated API response")
public class PaginatedResponseDto<T> {
    @Schema(description = "Response success status")
    private boolean success;

    @Schema(description = "Response message")
    private String message;

    @Schema(description = "Paginated data")
    private List<T> data;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    @Schema(description = "Error details (if any)")
    private String error;

    public static <T> PaginatedResponseDto<T> ok(String message, List<T> data, PaginationDto pagination) {
        return PaginatedResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .pagination(pagination)
                .build();
    }

    public static <T> PaginatedResponseDto<T> error(String message, String error) {
        return PaginatedResponseDto.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .build();
    }
}
