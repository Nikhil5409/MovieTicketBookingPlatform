package com.backendlld.movieticketbookingplatform.controller;

import com.backendlld.movieticketbookingplatform.dtos.BookTicketRequest;
import com.backendlld.movieticketbookingplatform.dtos.BookTicketResponse;
import com.backendlld.movieticketbookingplatform.dtos.ResponseStatus;
import com.backendlld.movieticketbookingplatform.exception.SeatsNotAvailableException;
import com.backendlld.movieticketbookingplatform.exception.ShowNotFoundException;
import com.backendlld.movieticketbookingplatform.model.Booking;
import com.backendlld.movieticketbookingplatform.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BookTicketResponse> bookTicket(@RequestBody BookTicketRequest request) {
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
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch(ShowNotFoundException exception){
            response.setMessage("Booking failed. "+exception.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }catch(SeatsNotAvailableException exception){
            response.setMessage("Booking failed. "+exception.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }catch(Exception exception){
            response.setMessage("Booking failed. "+exception.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}