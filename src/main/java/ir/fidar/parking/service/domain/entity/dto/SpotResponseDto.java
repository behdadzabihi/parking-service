package ir.fidar.parking.service.domain.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Parking spot details")
public class SpotResponseDto {
    @Schema(description = "Unique spot identifier", example = "1")
    private Long id;

    @Schema(description = "Spot number in the lot", example = "1")
    @JsonProperty("number")
    private Integer spotNumber;

    @Schema(description = "Current spot status", example = "AVAILABLE")
    private String status;

    @Schema(description = "Current reservation details (if any)")
    private ReservationInfoDto reservation;
}
