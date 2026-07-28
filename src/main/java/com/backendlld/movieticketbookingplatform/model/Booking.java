package com.backendlld.movieticketbookingplatform.model;

import com.backendlld.movieticketbookingplatform.model.Enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Booking extends BaseModel{
    private Date bookingDate;
    private int noOfSeats;
    @ManyToOne
    private User bookedBy;
    private int totalAmount;
    @ManyToMany
    private List<ShowSeat> bookedSeats;
    // ManyToMany coz in future we may support cancellations, then showSeat can be present in many bookings
    @OneToMany(mappedBy = "booking")
    private List<Payment> payments;
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
}
