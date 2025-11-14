package ir.fidar.parking.service.domain.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Reservation information")
public class ReservationInfoDto {
    @Schema(description = "Vehicle plate number", example = "ABC123")
    private String vehiclePlate;

    @Schema(description = "Reservation creation timestamp")
    private LocalDateTime reservedAt;
}