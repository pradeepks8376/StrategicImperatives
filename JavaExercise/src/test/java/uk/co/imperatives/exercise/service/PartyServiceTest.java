package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.imperatives.exercise.exception.ResourceNotFoundException;
import uk.co.imperatives.exercise.model.Guest;
import uk.co.imperatives.exercise.model.Table;
import uk.co.imperatives.exercise.repository.GuestRepository;
import uk.co.imperatives.exercise.repository.TableRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PartyServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private TableRepository tableRepository;

    @InjectMocks
    private PartyService partyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGuestArrivesSuccess() {
        // Arrange
        Guest guest = new Guest();
        guest.setName("John Doe");
        guest.setAccompanyingGuests(2);

        Table table = new Table();
        table.setTableNumber(1);
        table.setCapacity(10);
        table.setOccupiedSeats(5);

        guest.setTable(table);

        when(guestRepository.findByName("John Doe")).thenReturn(guest);
        when(tableRepository.save(any(Table.class))).thenReturn(table);
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        // Act
        Guest updatedGuest = partyService.guestArrives("John Doe", 3);

        // Assert
        assertNotNull(updatedGuest);
        assertTrue(updatedGuest.isArrived());
        assertEquals(8, table.getOccupiedSeats());
        verify(tableRepository, times(1)).save(table);
        verify(guestRepository, times(1)).save(guest);
    }

    @Test
    public void testGuestArrivesTableFull() {
        // Arrange
        Guest guest = new Guest();
        guest.setName("John Doe");
        guest.setAccompanyingGuests(2);

        Table table = new Table();
        table.setTableNumber(1);
        table.setCapacity(6); // Full table
        table.setOccupiedSeats(6);

        guest.setTable(table);

        when(guestRepository.findByName("John Doe")).thenReturn(guest);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            partyService.guestArrives("John Doe", 2);
        });

        assertEquals("Table 1 does not have enough space.", exception.getMessage());
        verify(tableRepository, never()).save(any(Table.class)); // Ensure save is not called
        verify(guestRepository, never()).save(any(Guest.class));
    }

    @Test
    public void testGuestNotFound() {
        // Arrange
        when(guestRepository.findByName("Unknown Guest")).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            partyService.guestArrives("Unknown Guest", 2);
        });

        verify(tableRepository, never()).save(any(Table.class)); // Ensure save is not called
        verify(guestRepository, never()).save(any(Guest.class));
    }

    @Test
    public void testTableNotFound() {
        // Arrange
        Guest guest = new Guest();
        guest.setName("John Doe");
        guest.setAccompanyingGuests(2);

        when(guestRepository.findByName("John Doe")).thenReturn(guest);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            partyService.guestArrives("John Doe", 2);
        });

        verify(tableRepository, never()).save(any(Table.class)); // Ensure save is not called
        verify(guestRepository, never()).save(any(Guest.class));
    }
}

