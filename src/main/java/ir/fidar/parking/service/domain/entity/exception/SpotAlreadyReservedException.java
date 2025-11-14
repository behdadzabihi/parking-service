package ir.fidar.parking.service.domain.entity.exception;

import lombok.Getter;

@Getter
public class SpotAlreadyReservedException extends ParkingDomainException {

    public SpotAlreadyReservedException(Long spotId, String currentVehiclePlate) {
        super(
                String.format("Spot %d is already reserved by vehicle %s", spotId, currentVehiclePlate),
                "SPOT_ALREADY_RESERVED",
                400
        );
    }

    public SpotAlreadyReservedException(Long spotId) {
        super(
                String.format("Spot %d is already reserved", spotId),
                "SPOT_ALREADY_RESERVED",
                400
        );
    }
}