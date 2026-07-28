package com.backendlld.movieticketbookingplatform.service;

import com.backendlld.movieticketbookingplatform.model.Booking;
import com.backendlld.movieticketbookingplatform.model.Enums.BookingStatus;
import com.backendlld.movieticketbookingplatform.model.Enums.ShowSeatStatus;
import com.backendlld.movieticketbookingplatform.model.Show;
import com.backendlld.movieticketbookingplatform.model.ShowSeat;
import com.backendlld.movieticketbookingplatform.model.User;
import com.backendlld.movieticketbookingplatform.repository.BookingRepository;
import com.backendlld.movieticketbookingplatform.repository.ShowRepository;
import com.backendlld.movieticketbookingplatform.repository.ShowSeatRepository;
import com.backendlld.movieticketbookingplatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private ShowRepository showRepository;
    private UserRepository userRepository;
    private ShowSeatRepository showSeatRepository;
    private BookingRepository bookingRepository;

    @Autowired
    public BookingService(ShowRepository showRepository, UserRepository userRepository, ShowSeatRepository showSeatRepository, BookingRepository bookingRepository) {
         this.showRepository = showRepository;
         this.userRepository = userRepository;
         this.showSeatRepository = showSeatRepository;
         this.bookingRepository = bookingRepository;
    }
    @Transactional()
    public Booking bookTicket(Long showId, Long userId, List<Long> showSeatIds){
        // 1. get user from db
        Optional<User> userOptional = userRepository.findById(userId);
        User user = null;
        if(userOptional.isEmpty()){
            user = new User();
            user.setUsername("User_"+userId);
            user = userRepository.save(user);
            // if we want to throw exception when user not found
            // throw new RuntimeException("User not found");
        }else{
            user = userOptional.get();
        }
        // 2. get show from db
        Optional<Show> showOptional = showRepository.findById(showId);
        if(showOptional.isEmpty()){
            throw new RuntimeException("No such show");
        }
        Show show = showOptional.get();
        // 3. get seats from db
        List<ShowSeat> showSeats = showSeatRepository.findAllByIdInAndStatus(showSeatIds, ShowSeatStatus.AVAILABLE);
        if(showSeats.size()<showSeatIds.size()){
            throw new RuntimeException("Certain seats are not available");
        }
        // 4. check if all seats are available
        // 5. If Yes, block the seats
        for(ShowSeat showSeat : showSeats){
            showSeat.setStatus(ShowSeatStatus.BLOCKED);
        }
        showSeatRepository.saveAll(showSeats);
        // 6. create the booking
        Booking booking = new Booking();
        booking.setBookedBy(user);
        booking.setBookingDate(new Date());
        booking.setBookedSeats(showSeats);
        booking.setBookingStatus(BookingStatus.PENDING);
        //get seatTypes
        //implement logic to get  total price of booking
        booking.setTotalAmount(100);
        booking.setNoOfSeats(showSeats.size());
        return bookingRepository.save(booking);
    }
}
