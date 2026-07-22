package com.backendlld.movieticketbookingplatform.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookTicketRequest {
    private long showId;
    private long userId;
    private List<Long> showSeats;

}
