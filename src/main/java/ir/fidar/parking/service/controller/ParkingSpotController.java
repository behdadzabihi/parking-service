package ir.fidar.parking.service.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.fidar.parking.service.domain.entity.SpotStatus;
import ir.fidar.parking.service.domain.entity.dto.*;
import ir.fidar.parking.service.domain.entity.exception.ParkingDomainException;
import ir.fidar.parking.service.mapper.PaginationMapper;
import ir.fidar.parking.service.mapper.SpotMapper;
import ir.fidar.prking.service.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/spots")
@Slf4j
@Tag(name = "Parking Spots", description = "APIs for managing parking spots and reservations")
public class ParkingSpotController {
    @Autowired
    private  ParkingSpotService parkingSpotService;
    @Autowired
    private  SpotMapper spotMapper;
    @Autowired
    private  PaginationMapper paginationMapper;

    @GetMapping
    @Operation(
            summary = "List all parking spots",
            description = "Retrieve all parking spots in the lot with their current status. Supports pagination."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all spots")
    public ResponseEntity<PaginatedResponseDto<SpotResponseDto>> getAllSpots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/spots - Fetching spots with pagination: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<SpotResponseDto> spotPage = parkingSpotService.getAllSpotsPageable(pageable)
                .map(spotMapper::spotToSpotResponseDto);

        PaginatedResponseDto<SpotResponseDto> response = PaginatedResponseDto.ok(
                "Spots retrieved successfully",
                spotPage.getContent(),
                paginationMapper.pageToPageDto(spotPage)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-status")
    @Operation(
            summary = "Get spots by status",
            description = "Retrieve parking spots filtered by their status (AVAILABLE or RESERVED)"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved spots")
    public ResponseEntity<PaginatedResponseDto<SpotResponseDto>> getSpotsByStatus(
            @RequestParam SpotStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/spots/by-status - Fetching spots by status: {}, page={}, size={}",
                status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<SpotResponseDto> spotPage = parkingSpotService.getSpotsByStatus(status, pageable)
                .map(spotMapper::spotToSpotResponseDto);

        PaginatedResponseDto<SpotResponseDto> response = PaginatedResponseDto.ok(
                String.format("Spots with status %s retrieved successfully", status),
                spotPage.getContent(),
                paginationMapper.pageToPageDto(spotPage)
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reserve")
    @Operation(
            summary = "Reserve a parking spot",
            description = "Reserve a specific parking spot for a vehicle"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spot reserved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - spot already reserved or invalid input"),
            @ApiResponse(responseCode = "404", description = "Spot not found")
    })
    public ResponseEntity<ApiResponseDto<SpotResponseDto>> reserveSpot(
            @PathVariable Long id,
            @RequestBody ReserveSpotRequestDto request) {

        log.info("POST /api/spots/{}/reserve - Reserving for vehicle: {}", id, request.getVehiclePlate());

        try {
            var spot = parkingSpotService.reserveSpot(id, request.getVehiclePlate());
            return ResponseEntity.ok(
                    ApiResponseDto.ok("Spot reserved successfully", spotMapper.spotToSpotResponseDto(spot))
            );
        } catch (ParkingDomainException e) {
            log.warn("Reservation failed: {}", e.getMessage());
            return ResponseEntity
                    .status(e.getHttpStatusCode())
                    .body(ApiResponseDto.error(e.getMessage(), e.getErrorCode()));
        }
    }

    @PostMapping("/{id}/release")
    @Operation(
            summary = "Release a parking spot",
            description = "Release a reserved parking spot back to available status"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spot released successfully"),
            @ApiResponse(responseCode = "404", description = "Spot not found")
    })
    public ResponseEntity<ApiResponseDto<SpotResponseDto>> releaseSpot(@PathVariable Long id) {
        log.info("POST /api/spots/{}/release - Releasing spot", id);

        try {
            var spot = parkingSpotService.releaseSpot(id);
            return ResponseEntity.ok(
                    ApiResponseDto.ok("Spot released successfully", spotMapper.spotToSpotResponseDto(spot))
            );
        } catch (ParkingDomainException e) {
            log.warn("Release failed: {}", e.getMessage());
            return ResponseEntity
                    .status(e.getHttpStatusCode())
                    .body(ApiResponseDto.error(e.getMessage(), e.getErrorCode()));
        }
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Get parking lot statistics",
            description = "Retrieve statistics about parking lot usage including occupancy rate"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    public ResponseEntity<ApiResponseDto<?>> getParkingStats() {
        log.info("GET /api/spots/stats - Fetching parking lot statistics");
        var stats = parkingSpotService.getParkingLotStats();
        return ResponseEntity.ok(ApiResponseDto.ok("Parking lot statistics retrieved", stats));
    }
}
