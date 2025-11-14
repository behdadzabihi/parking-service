package ir.fidar.parking.service.domain.entity.exception;

import lombok.Getter;


@Getter
public class SpotNotFoundException extends ParkingDomainException {

    public SpotNotFoundException(Long spotId) {
        super(
                String.format("Parking spot with id %d not found", spotId),
                "SPOT_NOT_FOUND",
                404
        );
    }

    public SpotNotFoundException(String message) {
        super(message, "SPOT_NOT_FOUND", 404);
    }
}