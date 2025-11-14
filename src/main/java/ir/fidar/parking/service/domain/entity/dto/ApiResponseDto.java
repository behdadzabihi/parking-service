package ir.fidar.parking.service.domain.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Standard API response")
public class ApiResponseDto<T> {

    @Schema(description = "Response success status")
    private boolean success;

    @Schema(description = "Response message")
    private String message;

    @Schema(description = "Response data")
    private T data;

    @Schema(description = "Error details (if any)")
    private String error;

    public static <T> ApiResponseDto<T> ok(String message, T data) {
        return ApiResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDto<T> error(String message, String error) {
        return ApiResponseDto.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .build();
    }
}