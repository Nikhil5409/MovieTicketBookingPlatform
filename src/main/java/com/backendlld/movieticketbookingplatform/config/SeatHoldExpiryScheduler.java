package com.backendlld.movieticketbookingplatform.config;

import com.backendlld.movieticketbookingplatform.model.Booking;
import com.backendlld.movieticketbookingplatform.model.Enums.BookingStatus;
import com.backendlld.movieticketbookingplatform.model.Enums.ShowSeatStatus;
import com.backendlld.movieticketbookingplatform.model.ShowSeat;
import com.backendlld.movieticketbookingplatform.repository.BookingRepository;
import com.backendlld.movieticketbookingplatform.repository.ShowSeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SeatHoldExpiryScheduler {

    private final BookingRepository bookingRepository;
    private final ShowSeatRepository showSeatRepository;

    public SeatHoldExpiryScheduler(BookingRepository bookingRepository, ShowSeatRepository showSeatRepository) {
        this.bookingRepository = bookingRepository;
        this.showSeatRepository = showSeatRepository;
    }

    @Scheduled(fixedRateString = "${booking.hold-check-interval-ms:30000}")
    @Transactional
    public void releaseExpiredHolds() {
        List<Booking> expiredBookings = bookingRepository.findByBookingStatusAndHoldExpiresAtBefore(BookingStatus.PENDING, new Date());
        for (Booking booking : expiredBookings) {
            List<ShowSeat> blockedSeats = booking.getBookedSeats().stream()
                    .filter(seat -> seat.getStatus() == ShowSeatStatus.BLOCKED)
                    .toList();
            for (ShowSeat seat : blockedSeats) {
                seat.setStatus(ShowSeatStatus.AVAILABLE);
            }
            showSeatRepository.saveAll(blockedSeats);
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            System.out.println("[SeatHoldExpiryScheduler] Released " + blockedSeats.size()
                    + " seat(s) for expired booking " + booking.getId());
        }
    }
}