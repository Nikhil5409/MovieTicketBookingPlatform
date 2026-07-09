package com.backendlld.movieticketbookingplatform.model;

import com.backendlld.movieticketbookingplatform.model.Enums.BookingStatus;
import com.backendlld.movieticketbookingplatform.model.Enums.PaymentMode;
import com.backendlld.movieticketbookingplatform.model.Enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Payment extends BaseModel{
    private Date date;
    private int amount;
    private String refNumber;
    @Enumerated
    private PaymentStatus Status;
    @Enumerated
    private PaymentMode paymentMode;
    @ManyToOne
    private Booking booking;
}
