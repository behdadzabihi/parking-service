package ir.fidar.parking.service.domain.entity.exception;

import lombok.Getter;

@Getter
public class InvalidVehiclePlateException extends ParkingDomainException {

    public InvalidVehiclePlateException(String vehiclePlate) {
        super(
                String.format("Invalid vehicle plate format: %s. " +
                        "Plate must be 3-10 alphanumeric characters.", vehiclePlate),
                "INVALID_VEHICLE_PLATE",
                400
        );
    }

    public InvalidVehiclePlateException(String vehiclePlate, String reason) {
        super(
                String.format("Invalid vehicle plate '%s': %s", vehiclePlate, reason),
                "INVALID_VEHICLE_PLATE",
                400
        );
    }
}