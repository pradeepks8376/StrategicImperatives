package uk.co.imperatives.exercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.imperatives.exercise.model.Guest;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByArrived(boolean arrived);
    Guest findByName(String name);
}