package com.backendlld.movieticketbookingplatform.service;

import com.backendlld.movieticketbookingplatform.exception.SeatsNotAvailableException;
import com.backendlld.movieticketbookingplatform.exception.ShowNotFoundException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private ShowRepository showRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShowSeatRepository showSeatRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookingService, "holdDurationMinutes", 10L);
    }

    @Test
    void bookTicket_blocksAvailableSeatsAndCreatesPendingBooking() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Show show = new Show();
        show.setId(1L);
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));

        ShowSeat seat1 = new ShowSeat();
        seat1.setId(10L);
        seat1.setStatus(ShowSeatStatus.AVAILABLE);
        ShowSeat seat2 = new ShowSeat();
        seat2.setId(11L);
        seat2.setStatus(ShowSeatStatus.AVAILABLE);
        List<Long> seatIds = List.of(10L, 11L);
        when(showSeatRepository.findAllByIdInAndStatus(seatIds, ShowSeatStatus.AVAILABLE))
                .thenReturn(List.of(seat1, seat2));
        when(showSeatRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.bookTicket(1L, 1L, seatIds);

        assertThat(booking.getBookingStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getNoOfSeats()).isEqualTo(2);
        assertThat(booking.getHoldExpiresAt()).isAfter(booking.getBookingDate());
        assertThat(seat1.getStatus()).isEqualTo(ShowSeatStatus.BLOCKED);
        assertThat(seat2.getStatus()).isEqualTo(ShowSeatStatus.BLOCKED);
    }

    @Test
    void bookTicket_throwsWhenShowDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(showRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.bookTicket(99L, 1L, List.of(10L)))
                .isInstanceOf(ShowNotFoundException.class);
    }

    @Test
    void bookTicket_throwsWhenSeatsAreNotAllAvailable() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Show show = new Show();
        show.setId(1L);
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));

        List<Long> seatIds = List.of(10L, 11L);
        ShowSeat onlyAvailableSeat = new ShowSeat();
        onlyAvailableSeat.setId(10L);
        when(showSeatRepository.findAllByIdInAndStatus(seatIds, ShowSeatStatus.AVAILABLE))
                .thenReturn(List.of(onlyAvailableSeat));

        assertThatThrownBy(() -> bookingService.bookTicket(1L, 1L, seatIds))
                .isInstanceOf(SeatsNotAvailableException.class);
    }
}