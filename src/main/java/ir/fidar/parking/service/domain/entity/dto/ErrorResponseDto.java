package ir.fidar.parking.service.domain.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Error response with detailed error information")
public class ErrorResponseDto {
    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error type or code", example = "SPOT_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Human-readable message")
    private String message;

    @Schema(description = "Request path", example = "/api/v1/spot/10")
    private String path;

    @Schema(description = "Timestamp of error", example = "2025-11-14T18:20:00")
    private LocalDateTime timestamp;
}