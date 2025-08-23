package naitei.group5.workingspacebooking.repository;

import naitei.group5.workingspacebooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {}
