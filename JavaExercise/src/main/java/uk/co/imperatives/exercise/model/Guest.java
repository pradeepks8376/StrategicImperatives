package uk.co.imperatives.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int accompanyingGuests;
    private boolean arrived;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private Table table;

}

