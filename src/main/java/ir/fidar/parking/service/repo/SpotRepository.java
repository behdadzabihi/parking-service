package ir.fidar.parking.service.repo;



import ir.fidar.parking.service.domain.entity.Spot;
import ir.fidar.parking.service.domain.entity.SpotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

    List<Spot> findByStatus(SpotStatus status);

    Page<Spot> findByStatus(SpotStatus status, Pageable pageable);

    Page<Spot> findAllByOrderBySpotNumberAsc(Pageable pageable);

}
