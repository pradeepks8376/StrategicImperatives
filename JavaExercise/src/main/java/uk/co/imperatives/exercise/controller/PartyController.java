package uk.co.imperatives.exercise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.imperatives.exercise.model.Guest;
import uk.co.imperatives.exercise.service.PartyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PartyController {

    @Autowired
    private PartyService partyService;

    @PostMapping("/guest_list")
    public ResponseEntity<Guest> addGuest(@RequestBody Guest guest) {
        return ResponseEntity.ok(partyService.addGuest(guest));
    }

    @GetMapping("/guest_list")
    public ResponseEntity<List<Guest>> getGuestList() {
        return ResponseEntity.ok(partyService.getGuestList());
    }

    @PutMapping("/guests/{name}")
    public ResponseEntity<Guest> guestArrives(@PathVariable String name, @RequestBody int extraGuests) {
        return ResponseEntity.ok(partyService.guestArrives(name, extraGuests));
    }

    @DeleteMapping("/guests/{name}")
    public ResponseEntity<Void> guestLeaves(@PathVariable String name) {
        partyService.guestLeaves(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/guests")
    public ResponseEntity<List<Guest>> getArrivedGuests() {
        return ResponseEntity.ok(partyService.getArrivedGuests());
    }

    @GetMapping("/seats_empty")
    public ResponseEntity<Integer> countEmptySeats() {
        return ResponseEntity.ok(partyService.countEmptySeats());
    }
}

