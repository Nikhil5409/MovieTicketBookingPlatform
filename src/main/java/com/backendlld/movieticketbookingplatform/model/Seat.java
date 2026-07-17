package com.backendlld.movieticketbookingplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Seat extends BaseModel {
    private int seatRow;
    private int seatColumn;
    @ManyToOne
    private SeatType seatType;
    private String seatName;
}
