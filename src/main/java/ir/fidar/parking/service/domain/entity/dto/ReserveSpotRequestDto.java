package ir.fidar.parking.service.domain.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to reserve a parking spot")
public class ReserveSpotRequestDto {
    @Schema(description = "Vehicle license plate", example = "XYZ789", required = true)
    private String vehiclePlate;
}