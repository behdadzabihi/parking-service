package ir.fidar.parking.service.mapper;

import ir.fidar.parking.service.domain.entity.Reservation;
import ir.fidar.parking.service.domain.entity.Spot;
import ir.fidar.parking.service.domain.entity.SpotStatus;
import ir.fidar.parking.service.domain.entity.dto.ReservationInfoDto;
import ir.fidar.parking.service.domain.entity.dto.SpotResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Named;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface SpotMapper {

    @Mapping(source = "spotNumber", target = "spotNumber")
    @Mapping(source = "status", target = "status", qualifiedByName = "spotStatusToString")
    @Mapping(source = "reservation", target = "reservation", qualifiedByName = "mapReservation")
    SpotResponseDto spotToSpotResponseDto(Spot spot);

    @Mapping(source = "vehiclePlate", target = "vehiclePlate")
    @Mapping(source = "createdAt", target = "reservedAt")
    ReservationInfoDto reservationToReservationInfoDto(Reservation reservation);


    @Named("mapReservation")
    default ReservationInfoDto mapReservation(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        return reservationToReservationInfoDto(reservation);
    }

    @Named("spotStatusToString")
    default String mapSpotStatus(SpotStatus status) {
        if (status == null) {
            return null;
        }
        return status.toString();
    }
}