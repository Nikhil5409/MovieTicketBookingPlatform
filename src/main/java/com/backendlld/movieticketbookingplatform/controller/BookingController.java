package com.backendlld.movieticketbookingplatform.controller;

import com.backendlld.movieticketbookingplatform.dtos.BookTicketRequest;
import com.backendlld.movieticketbookingplatform.dtos.BookTicketResponse;
import com.backendlld.movieticketbookingplatform.dtos.ResponseStatus;
import com.backendlld.movieticketbookingplatform.model.Booking;
import com.backendlld.movieticketbookingplatform.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {
    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public BookTicketResponse bookTicket(@RequestBody BookTicketRequest request) {
        BookTicketResponse response = new BookTicketResponse();
        try{
            Booking booking = bookingService.bookTicket(
                    request.getShowId(),
                    request.getUserId(),
                    request.getShowSeats()
            );
            response.setBookingId(booking.getId());
            response.setStatus(ResponseStatus.SUCCESS);
            response.setMessage("Booking Confirmed. Please make payment!");
        }catch(Exception exception){
            response.setMessage("Booking failed. "+exception.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }
}