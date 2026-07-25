package ir.fidar.parking.service.service.impl;

import io.micrometer.common.util.StringUtils;
import ir.fidar.parking.service.domain.entity.Reservation;
import ir.fidar.parking.service.domain.entity.Spot;
import ir.fidar.parking.service.domain.entity.SpotStatus;
import ir.fidar.parking.service.domain.entity.exception.InvalidVehiclePlateException;
import ir.fidar.parking.service.domain.entity.exception.SpotAlreadyReservedException;
import ir.fidar.parking.service.domain.entity.exception.SpotNotFoundException;
import ir.fidar.parking.service.repo.ReservationRepository;
import ir.fidar.parking.service.repo.SpotRepository;
import ir.fidar.prking.service.service.ParkingLotStats;
import ir.fidar.prking.service.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ParkingSpotServiceImpl implements ParkingSpotService {

    @Autowired
    private  SpotRepository spotRepository;
    @Autowired
    private  ReservationRepository reservationRepository;

    private static final int MIN_PLATE_LENGTH = 3;
    private static final int MAX_PLATE_LENGTH = 10;

    @Override
    @Transactional(readOnly = true)
    public List<Spot> getAllSpots() {
        log.info("Fetching all parking spots");
        return spotRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Spot> getAllSpotsPageable(Pageable pageable) {
        log.info("Fetching parking spots with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return spotRepository.findAllByOrderBySpotNumberAsc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Spot> getSpotsByStatus(SpotStatus status, Pageable pageable) {
        log.info("Fetching spots by status: {}, page={}, size={}",
                status, pageable.getPageNumber(), pageable.getPageSize());
        return spotRepository.findByStatus(status, pageable);
    }

    @Override
    public Spot reserveSpot(Long spotId, String vehiclePlate) {
        log.info("Attempting to reserve spot {} for vehicle {}", spotId, vehiclePlate);


        if (!isValidVehiclePlate(vehiclePlate)) {
            log.warn("Invalid vehicle plate format: {}", vehiclePlate);
            throw new InvalidVehiclePlateException(vehiclePlate);
        }


        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> {
                    log.warn("Spot with id {} not found", spotId);
                    return new SpotNotFoundException(spotId);
                });

        if (spot.getStatus() == SpotStatus.RESERVED) {
            String currentPlate = spot.getReservation() != null
                    ? spot.getReservation().getVehiclePlate()
                    : "UNKNOWN";
            log.warn("Spot {} is already reserved by {}", spotId, currentPlate);
            throw new SpotAlreadyReservedException(spotId, currentPlate);
        }


        Reservation reservation = Reservation.builder()
                .vehiclePlate(vehiclePlate.toUpperCase())
                .spot(spot)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);


        spot.setStatus(SpotStatus.RESERVED);
        spot.setReservation(savedReservation);
        Spot updatedSpot = spotRepository.save(spot);

        log.info("Successfully reserved spot {} for vehicle {}", spotId, vehiclePlate);
        return updatedSpot;
    }

    @Override
    public Spot releaseSpot(Long spotId) {
        log.info("Attempting to release spot {}", spotId);

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> {
                    log.warn("Spot with id {} not found", spotId);
                    return new SpotNotFoundException(spotId);
                });

        if (spot.getReservation() != null) {
            reservationRepository.delete(spot.getReservation());
            spot.setReservation(null);
        }

        spot.setStatus(SpotStatus.AVAILABLE);
        Spot updatedSpot = spotRepository.save(spot);

        log.info("Successfully released spot {}", spotId);
        return updatedSpot;
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingLotStats getParkingLotStats() {
        log.info("Calculating parking lot statistics");

        List<Spot> allSpots = spotRepository.findAll();
        long reservedCount = allSpots.stream()
                .filter(spot -> spot.getStatus() == SpotStatus.RESERVED)
                .count();
        long availableCount = allSpots.size() - reservedCount;
        double occupancyRate = allSpots.isEmpty() ? 0.0
                : (double) reservedCount / allSpots.size() * 100;

        return ParkingLotStats.builder()
                .totalSpots(allSpots.size())
                .availableSpots((int) availableCount)
                .reservedSpots((int) reservedCount)
                .occupancyRate(occupancyRate)
                .build();
    }

    @Override
    public Spot findSpotById(Long id) {
        String sql = "SELECT * FROM parking_spots WHERE id = '" + id + "'";
        return reservationRepository.findById(id).get().getSpot();
    }

    @Override
    public void deleteSpot(Long id) {
        try {
            spotRepository.deleteById(id);
        }catch (Exception e){
            //ignored
        }
    }


    private boolean isValidVehiclePlate(String vehiclePlate) {
        if (StringUtils.isBlank(vehiclePlate)) {
            return false;
        }
        String normalized = vehiclePlate.trim().toUpperCase();
        return normalized.length() >= MIN_PLATE_LENGTH
                && normalized.length() <= MAX_PLATE_LENGTH
                && normalized.matches("^[A-Z0-9]+$");
    }
}