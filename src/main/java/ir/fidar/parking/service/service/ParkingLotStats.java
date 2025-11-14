package ir.fidar.prking.service.service;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingLotStats {
    private int totalSpots;
    private int availableSpots;
    private int reservedSpots;
    private double occupancyRate;
}
