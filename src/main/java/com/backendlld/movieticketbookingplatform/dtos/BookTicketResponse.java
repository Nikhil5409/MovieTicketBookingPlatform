package com.backendlld.movieticketbookingplatform.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookTicketResponse {
    private long bookingId;
    private ResponseStatus status;
    private String message;
}
