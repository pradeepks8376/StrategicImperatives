package uk.co.imperatives.exercise.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.co.imperatives.exercise.exception.ResourceNotFoundException;
import uk.co.imperatives.exercise.model.Guest;
import uk.co.imperatives.exercise.model.Table;
import uk.co.imperatives.exercise.service.PartyService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PartyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PartyService partyService;

    @InjectMocks
    private PartyController partyController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(partyController).build();
    }

    @Test
    public void testGuestArrivesSuccess() throws Exception {
        // Arrange
        Table table = new Table();
        table.setTableNumber(1);
        table.setCapacity(10);
        table.setOccupiedSeats(5);

        Guest guest = new Guest();
        guest.setName("John Doe");
        guest.setArrived(true);
        guest.setAccompanyingGuests(2);
        guest.setTable(table);

        when(partyService.guestArrives(anyString(), anyInt())).thenReturn(guest);

        // Act & Assert
        mockMvc.perform(post("/api/guests/arrive")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\", \"additionalGuests\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.arrived").value(true))
                .andExpect(jsonPath("$.accompanyingGuests").value(2))
                .andExpect(jsonPath("$.table.tableNumber").value(1));
    }

    @Test
    public void testGuestArrivesTableFull() throws Exception {
        // Arrange
        Table table = new Table();
        table.setTableNumber(1);
        table.setCapacity(6);  // Table is already full
        table.setOccupiedSeats(6);

        Guest guest = new Guest();
        guest.setName("John Doe");
        guest.setArrived(false);
        guest.setAccompanyingGuests(2);
        guest.setTable(table);

        when(partyService.guestArrives(anyString(), anyInt())).thenThrow(new IllegalStateException("Table 1 does not have enough space."));

        // Act & Assert
        mockMvc.perform(post("/api/guests/arrive")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\", \"additionalGuests\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Table 1 does not have enough space."));
    }

    @Test
    public void testGuestNotFound() throws Exception {
        // Arrange
        when(partyService.guestArrives(anyString(), anyInt())).thenThrow(new ResourceNotFoundException("Guest not found"));

        // Act & Assert
        mockMvc.perform(post("/api/guests/arrive")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Unknown Guest\", \"additionalGuests\":2}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Guest not found"));
    }
}


