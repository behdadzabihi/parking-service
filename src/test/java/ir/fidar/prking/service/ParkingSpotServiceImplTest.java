package ir.fidar.prking.service;

import ir.fidar.parking.service.domain.entity.Reservation;
import ir.fidar.parking.service.domain.entity.Spot;
import ir.fidar.parking.service.domain.entity.SpotStatus;
import ir.fidar.parking.service.domain.entity.exception.InvalidVehiclePlateException;
import ir.fidar.parking.service.domain.entity.exception.SpotAlreadyReservedException;
import ir.fidar.parking.service.domain.entity.exception.SpotNotFoundException;
import ir.fidar.parking.service.repo.ReservationRepository;
import ir.fidar.parking.service.repo.SpotRepository;
import ir.fidar.parking.service.service.impl.ParkingSpotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Parking Spot Service Tests")
class ParkingSpotServiceImplTest {
    @Mock
    private SpotRepository spotRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ParkingSpotServiceImpl parkingSpotService;

    private Spot availableSpot;
    private Spot reservedSpot;

    @BeforeEach
    void setUp() {
        availableSpot = Spot.builder()
                .id(1L)
                .spotNumber(1)
                .status(SpotStatus.AVAILABLE)
                .build();

        reservedSpot = Spot.builder()
                .id(2L)
                .spotNumber(2)
                .status(SpotStatus.RESERVED)
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .vehiclePlate("ABC123")
                .spot(reservedSpot)
                .createdAt(LocalDateTime.now())
                .build();

        reservedSpot.setReservation(reservation);
    }

    @Test
    @DisplayName("Should successfully reserve an available spot with valid plate")
    void testReserveSpot_Success() {
        String vehiclePlate = "XYZ789";
        when(spotRepository.findById(1L)).thenReturn(Optional.of(availableSpot));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation res = invocation.getArgument(0);
                    res.setId(10L);
                    res.setCreatedAt(LocalDateTime.now());
                    return res;
                });
        when(spotRepository.save(any(Spot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Spot result = parkingSpotService.reserveSpot(1L, vehiclePlate);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(SpotStatus.RESERVED);
        assertThat(result.getReservation()).isNotNull();
        assertThat(result.getReservation().getVehiclePlate()).isEqualTo(vehiclePlate.toUpperCase());

        verify(spotRepository).findById(1L);
        verify(reservationRepository).save(any(Reservation.class));
        verify(spotRepository).save(availableSpot);
    }

    @Test
    @DisplayName("Should fail when trying to reserve an already reserved spot")
    void testReserveSpot_AlreadyReserved() {
        when(spotRepository.findById(2L)).thenReturn(Optional.of(reservedSpot));

        assertThatThrownBy(() -> parkingSpotService.reserveSpot(2L, "DEF456"))
                .isInstanceOf(SpotAlreadyReservedException.class)
                .hasMessageContaining("already reserved");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail with SpotNotFoundException when spot does not exist")
    void testReserveSpot_SpotNotFound() {
        when(spotRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parkingSpotService.reserveSpot(999L, "ABC123"))
                .isInstanceOf(SpotNotFoundException.class)
                .hasMessageContaining("not found");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail with InvalidVehiclePlateException for invalid plate format")
    void testReserveSpot_InvalidVehiclePlate() {

        assertThatThrownBy(() -> parkingSpotService.reserveSpot(1L, ""))
                .isInstanceOf(InvalidVehiclePlateException.class);

        assertThatThrownBy(() -> parkingSpotService.reserveSpot(1L, "AB"))
                .isInstanceOf(InvalidVehiclePlateException.class);

        assertThatThrownBy(() -> parkingSpotService.reserveSpot(1L, "AB@123"))
                .isInstanceOf(InvalidVehiclePlateException.class);
    }

    @Test
    @DisplayName("Should successfully release a reserved spot")
    void testReleaseSpot_Success() {
        when(spotRepository.findById(2L)).thenReturn(Optional.of(reservedSpot));
        when(spotRepository.save(any(Spot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Spot result = parkingSpotService.releaseSpot(2L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(SpotStatus.AVAILABLE);
        assertThat(result.getReservation()).isNull();

        verify(spotRepository).findById(2L);
        verify(reservationRepository).delete(any(Reservation.class));
        verify(spotRepository).save(reservedSpot);
    }

    @Test
    @DisplayName("Should successfully release an already available spot")
    void testReleaseSpot_AlreadyAvailable() {
        when(spotRepository.findById(1L)).thenReturn(Optional.of(availableSpot));
        when(spotRepository.save(any(Spot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Spot result = parkingSpotService.releaseSpot(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(SpotStatus.AVAILABLE);
        assertThat(result.getReservation()).isNull();

        verify(reservationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return paginated results when fetching all spots")
    void testGetAllSpotsPageable_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Spot> spotPage = new PageImpl<>(Arrays.asList(availableSpot, reservedSpot), pageable, 2);
        when(spotRepository.findAllByOrderBySpotNumberAsc(pageable)).thenReturn(spotPage);

        Page<Spot> result = parkingSpotService.getAllSpotsPageable(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);

        verify(spotRepository).findAllByOrderBySpotNumberAsc(pageable);
    }

    @Test
    @DisplayName("Should return parking lot statistics correctly")
    void testGetParkingLotStats_Success() {
        when(spotRepository.findAll()).thenReturn(Arrays.asList(availableSpot, reservedSpot));

        ir.fidar.prking.service.service.ParkingLotStats stats = parkingSpotService.getParkingLotStats();

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalSpots()).isEqualTo(2);
        assertThat(stats.getAvailableSpots()).isEqualTo(1);
        assertThat(stats.getReservedSpots()).isEqualTo(1);
        assertThat(stats.getOccupancyRate()).isEqualTo(50.0);

        verify(spotRepository).findAll();
    }
}