package naitei.group5.workingspacebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import naitei.group5.workingspacebooking.entity.VenueStyle;

import java.util.Optional;

@Repository
public interface VenueStyleRepository extends JpaRepository<VenueStyle, Integer> {

    Optional<VenueStyle> findByName(String name);
}
