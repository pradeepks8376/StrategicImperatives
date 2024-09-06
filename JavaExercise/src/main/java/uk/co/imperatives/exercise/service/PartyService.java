package uk.co.imperatives.exercise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.exception.ResourceNotFoundException;
import uk.co.imperatives.exercise.model.Guest;
import uk.co.imperatives.exercise.model.Table;
import uk.co.imperatives.exercise.repository.GuestRepository;
import uk.co.imperatives.exercise.repository.TableRepository;

import java.util.List;

@Service
public class PartyService {
    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private TableRepository tableRepository;

    public Guest addGuest(Guest guest) {
        return guestRepository.save(guest);
    }

    public List<Guest> getGuestList() {
        return guestRepository.findAll();
    }

    public Guest guestArrives(String name, int additionalGuests) {
        // Fetch the guest by name
        Guest guest = guestRepository.findByName(name);
        if (guest == null) {
            throw new ResourceNotFoundException("Guest not found");
        }

        // Fetch the table information
        Table table = guest.getTable();
        if (table == null) {
            throw new ResourceNotFoundException("Table not found");
        }

        // Calculate the new total number of occupied seats
        int totalGuestsAfterArrival = table.getOccupiedSeats() + additionalGuests + 1; // +1 for the guest themselves

        // Check if the table has space
        if (totalGuestsAfterArrival > table.getCapacity()) {
            throw new IllegalStateException("Table " + table.getTableNumber() + " does not have enough space.");
        }

        // Update the occupied seats and mark guest as arrived
        table.setOccupiedSeats(totalGuestsAfterArrival);
        guest.setArrived(true);
        guest.setAccompanyingGuests(guest.getAccompanyingGuests() + additionalGuests);

        // Save the updated table and guest
        tableRepository.save(table);
        return guestRepository.save(guest);
    }

    public void guestLeaves(String name) {
        Guest guest = guestRepository.findByName(name);
        if (guest == null) {
            throw new ResourceNotFoundException("Guest not found");
        }
        guestRepository.delete(guest);
    }

    public List<Guest> getArrivedGuests() {
        return guestRepository.findByArrived(true);
    }

    public int countEmptySeats() {
        int totalSeats = 0 /* Total seats calculation */;
        int occupiedSeats = guestRepository.findByArrived(true).stream()
                .mapToInt(Guest::getAccompanyingGuests).sum();
        return totalSeats - occupiedSeats;
    }
}

