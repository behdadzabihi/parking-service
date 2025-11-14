package ir.fidar.parking.service.domain.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Pagination metadata")
public class PaginationDto {
    @Schema(description = "Current page number (0-indexed)", example = "0")
    private int page;

    @Schema(description = "Page size", example = "10")
    private int size;

    @Schema(description = "Total number of elements", example = "25")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "3")
    private int totalPages;

    @Schema(description = "Is this the first page", example = "true")
    private boolean first;

    @Schema(description = "Is this the last page", example = "false")
    private boolean last;

    @Schema(description = "Number of elements on current page", example = "10")
    private int numberOfElements;
}