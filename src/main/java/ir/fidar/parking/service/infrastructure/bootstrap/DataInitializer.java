package ir.fidar.parking.service.infrastructure.bootstrap;



import ir.fidar.parking.service.domain.entity.Spot;
import ir.fidar.parking.service.domain.entity.SpotStatus;
import ir.fidar.parking.service.repo.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final SpotRepository spotRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing parking lot with sample data...");


        for (int i = 1; i <= 5; i++) {
            Spot spot = Spot.builder()
                    .spotNumber(i)
                    .status(SpotStatus.AVAILABLE)
                    .build();
            spotRepository.save(spot);
            log.info("Created parking spot: {}", i);
        }

        log.info("Parking lot initialization completed successfully");
    }
}