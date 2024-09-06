package uk.co.imperatives.exercise.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.imperatives.exercise.model.Table;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {

    // Custom query method to find a table by its table number
    Table findByTableNumber(int tableNumber);
}
