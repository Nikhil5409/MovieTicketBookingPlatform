package com.backendlld.movieticketbookingplatform.controller;

import com.backendlld.movieticketbookingplatform.dtos.BookTicketRequest;
import com.backendlld.movieticketbookingplatform.dtos.BookTicketResponse;
import com.backendlld.movieticketbookingplatform.dtos.ResponseStatus;
import com.backendlld.movieticketbookingplatform.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BookingController {
    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    public BookTicketResponse bookTicket(BookTicketRequest request) {
        BookTicketResponse response = new BookTicketResponse();
        try{
            bookingService.bookTicket(
                    request.getShowId(),
                    request.getUserId(),
                    request.getShowSeats()
            );
            response.setBookingId(response.getBookingId());
            response.setStatus(ResponseStatus.SUCCESS);
            response.setMessage("Booking Confirmed. Please make payment!");
        }catch(Exception exception){
            response.setMessage("Booking failed. "+exception.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }
}



//BookTicket => Booking