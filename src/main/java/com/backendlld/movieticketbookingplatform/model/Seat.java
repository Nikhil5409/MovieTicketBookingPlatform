package com.backendlld.movieticketbookingplatform.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Seat extends BaseModel {
    private int row;
    private int column;
    private SeatType seatType;
    private String seatName;
}
