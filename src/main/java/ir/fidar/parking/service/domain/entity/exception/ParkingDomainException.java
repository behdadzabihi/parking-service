package ir.fidar.parking.service.domain.entity.exception;

import lombok.Getter;


@Getter
public abstract class ParkingDomainException extends RuntimeException {
    private final String errorCode;
    private final int httpStatusCode;

    public ParkingDomainException(String message, String errorCode, int httpStatusCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    public ParkingDomainException(String message, String errorCode, int httpStatusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }
}