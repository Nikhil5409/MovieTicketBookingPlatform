package com.backendlld.movieticketbookingplatform.config;

import com.backendlld.movieticketbookingplatform.model.*;
import com.backendlld.movieticketbookingplatform.model.Enums.Features;
import com.backendlld.movieticketbookingplatform.model.Enums.Languages;
import com.backendlld.movieticketbookingplatform.model.Enums.ShowSeatStatus;
import com.backendlld.movieticketbookingplatform.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final SeatTypeRepository seatTypeRepository;
    private final RegionRepository regionRepository;
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final UserRepository userRepository;

    public DataSeeder(SeatTypeRepository seatTypeRepository, RegionRepository regionRepository,
                       ActorRepository actorRepository, MovieRepository movieRepository,
                       SeatRepository seatRepository, ScreenRepository screenRepository,
                       TheatreRepository theatreRepository, ShowRepository showRepository,
                       ShowSeatRepository showSeatRepository, UserRepository userRepository) {
        this.seatTypeRepository = seatTypeRepository;
        this.regionRepository = regionRepository;
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
        this.seatRepository = seatRepository;
        this.screenRepository = screenRepository;
        this.theatreRepository = theatreRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (showRepository.count() > 0) {
            System.out.println("[DataSeeder] Seed data already present, skipping.");
            return;
        }

        SeatType regular = new SeatType();
        regular.setName("Regular");
        regular = seatTypeRepository.save(regular);

        Region region = new Region();
        region.setName("Bangalore");
        region = regionRepository.save(region);

        Actor actor = new Actor();
        actor.setName("Sample Actor");
        actor = actorRepository.save(actor);

        Movie movie = new Movie();
        movie.setTitle("Sample Movie");
        movie.setDirector("Sample Director");
        movie.setYear("2026");
        movie.setGenre("Action");
        movie.setRating(4.5);
        movie.setActors(List.of(actor));
        movie.setLanguages(List.of(Languages.ENGLISH));
        movie.setFeatures(List.of(Features.Two_D));
        movie = movieRepository.save(movie);

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Seat seat = new Seat();
            seat.setSeatRow(1);
            seat.setSeatColumn(i);
            seat.setSeatName("A" + i);
            seat.setSeatType(regular);
            seats.add(seat);
        }
        seats = seatRepository.saveAll(seats);

        Screen screen = new Screen();
        screen.setName("Screen 1");
        screen.setScreenFeatures(List.of(Features.Two_D));
        screen.setSeats(seats);
        screen = screenRepository.save(screen);

        Theatre theatre = new Theatre();
        theatre.setName("Sample Theatre");
        theatre.setAddress("123 Main Street");
        theatre.setRegion(region);
        theatre.setRating(4.2);
        theatre.setScreens(List.of(screen));
        theatre.setMovies(List.of(movie));
        theatre = theatreRepository.save(theatre);

        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setTheatre(theatre);
        show.setDate(new Date());
        show.setLanguage(Languages.ENGLISH);
        show.setFeatures(List.of(Features.Two_D));
        show = showRepository.save(show);

        List<ShowSeat> showSeats = new ArrayList<>();
        for (Seat seat : seats) {
            ShowSeat showSeat = new ShowSeat();
            showSeat.setShow(show);
            showSeat.setSeat(seat);
            showSeat.setStatus(ShowSeatStatus.AVAILABLE);
            showSeats.add(showSeat);
        }
        showSeats = showSeatRepository.saveAll(showSeats);

        User user = new User();
        user.setUsername("test_user");
        user.setEmail("test_user@example.com");
        user = userRepository.save(user);

        List<Long> showSeatIds = showSeats.stream().map(BaseModel::getId).toList();

        System.out.println("=================================================");
        System.out.println("[DataSeeder] Seed data created for testing bookTicket:");
        System.out.println("  userId  : " + user.getId());
        System.out.println("  showId  : " + show.getId());
        System.out.println("  showSeatIds (AVAILABLE): " + showSeatIds);
        System.out.println("  Sample request body for POST /bookings:");
        System.out.println("  {\"showId\": " + show.getId() + ", \"userId\": " + user.getId()
                + ", \"showSeats\": " + showSeatIds + "}");
        System.out.println("=================================================");
    }
}