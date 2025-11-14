package ir.fidar.prking.service.service;


import ir.fidar.parking.service.domain.entity.Spot;
import ir.fidar.parking.service.domain.entity.SpotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;



public interface ParkingSpotService {

    List<Spot> getAllSpots();

    Page<Spot> getAllSpotsPageable(Pageable pageable);

    Page<Spot> getSpotsByStatus(SpotStatus status, Pageable pageable);

    Spot reserveSpot(Long spotId, String vehiclePlate);

    Spot releaseSpot(Long spotId);

    ir.fidar.prking.service.service.ParkingLotStats getParkingLotStats();
}
