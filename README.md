The Application
It's a movie ticket booking backend (Spring Boot, Java 21). Core idea: theatres have screens, screens have seats, movies run as shows on those screens at specific times, and users book specific seats for a specific show.

1. Spring Boot, briefly
Spring Boot is a framework that removes most of the manual wiring older "Spring" projects needed. Three things matter here:

@SpringBootApplication (on MovieTicketBookingPlatformApplication) — one annotation that triggers auto-configuration, component scanning, and starts an embedded web server (Tomcat) when you run main(). There's no separate server to install or deploy to.
Starters (in pom.xml): spring-boot-starter-web gives you the REST/MVC machinery; spring-boot-starter-data-jpa gives you ORM (object-relational mapping) via Hibernate, so Java classes map to database tables without hand-written SQL.
Dependency Injection — classes don't construct their own dependencies with new; Spring builds them and hands them in via constructor. You see this everywhere below as @Autowired constructors.
2. "MVC" in a REST backend
Classic MVC is Model–View–Controller. This app has no View (no HTML templates) — it's a REST API, so the flow is really:

Controller  →  Service  →  Repository  →  Database
 (HTTP)        (logic)      (data access)
     ↑
  Model (entities/DTOs used throughout)
Each incoming HTTP request hits a Controller, which delegates business logic to a Service, which talks to the database through a Repository, all operating on Model classes. Let's walk each layer as it's actually built in this repo.

3. Model layer — model/
These are JPA entities — plain Java classes annotated so Hibernate can map them straight to database tables (no SQL written by hand; spring.jpa.hibernate.ddl-auto=update even auto-creates/updates the schema from these classes).

BaseModel — abstract parent (@MappedSuperclass) giving every entity id, createdAt, updatedAt. Avoids repeating those fields.
Movie — title, director, genre, rating, @ManyToMany list of Actors, list of Languages/Features enums.
Theatre — belongs to a Region, has a list of Screens and the Movies it's showing.
Screen — belongs to a theatre, has a list of physical Seats.
Seat — a physical seat (row/column/name) with a SeatType (e.g. Regular, Premium, Recliner).
Show — a specific Movie playing on a specific Screen/Theatre at a Date. This is the thing users actually book against.
ShowSeat — the per-show instance of a physical Seat, carrying a ShowSeatStatus (AVAILABLE/BLOCKED/BOOKED, etc). This indirection matters: a Seat is a fixed chair; a ShowSeat is "that chair, for this 7pm show" — its availability resets per show.
ShowSeatType — the price of a given SeatType for a given Show (e.g. Premium seats cost more for a Friday night show than a Tuesday matinee).
User — has many Bookings.
Booking — links a User to the ShowSeats they reserved, a total amount, a BookingStatus (PENDING/CONFIRMED/etc), and its Payments.
Payment — amount, date, PaymentStatus, PaymentMode, linked back to a Booking.
Enums (model/Enums/) — BookingStatus, PaymentStatus, PaymentMode, ShowSeatStatus, Languages, Features — fixed sets of valid values used across entities instead of raw strings.
This is the vocabulary the rest of the app is built on.

4. Repository layer — repository/
Interfaces extending Spring Data JPA's JpaRepository. You declare a method signature and Spring generates the SQL/implementation for you — no manual query-writing for standard cases.

UserRepository, ShowRepository, BookingRepository — standard CRUD, nothing custom.
ShowSeatRepository — has one custom method, findAllByIdInAndStatus(ids, status), which Spring Data translates into a WHERE id IN (...) AND status = ... query just from the method name. This is how the booking flow checks "are these specific seats still available?" in one query.
Only these four exist — there's no MovieRepository, TheatreRepository, PaymentRepository, etc. yet, which is why you can't browse movies/theatres through the API today.

5. Service layer — service/BookingService.java
This is where business logic lives — Controllers should stay "dumb" and just handle HTTP; Services own the actual rules. Right now there's exactly one: BookingService.bookTicket(showId, userId, showSeatIds), and it runs a sequence:

Look up the User (creates a throwaway one if missing — a placeholder, not real auth).
Look up the Show; throw if it doesn't exist.
Fetch the requested ShowSeats but only those currently AVAILABLE via that repository method. If fewer come back than requested, someone else already grabbed one — fail with "seats not available."
Mark those seats BLOCKED and save them.
Create a Booking record (status PENDING) linking user, seats, and a (currently hardcoded) total amount.
This method is annotated @Service so Spring registers it as a bean and injects the four repositories it needs into its constructor.

6. DTOs — dtos/
DTOs (Data Transfer Objects) shape what crosses the HTTP boundary, kept separate from the internal Model entities so the API contract doesn't leak database structure:

BookTicketRequest — what the client sends (showId, userId, showSeats).
BookTicketResponse — what comes back (bookingId, status, message).
ResponseStatus — a small enum (SUCCESS/FAILURE) used in the response.
7. Controller layer — controller/BookingController.java
The HTTP entry point. @RestController tells Spring "every method's return value gets serialized straight to JSON in the HTTP response" (no view templates). @PostMapping("/bookings") maps POST /bookings requests here.

The method: deserializes the request body into BookTicketRequest (@RequestBody), calls bookingService.bookTicket(...), wraps the result (or any exception) into a BookTicketResponse, and returns it. This is the only endpoint currently exposed.

8. End-to-end trace of the one working flow
Client → POST /bookings {showId, userId, showSeats:[...]}
   → BookingController.bookTicket()
       → BookingService.bookTicket()
           → UserRepository.findById()      (get/create user)
           → ShowRepository.findById()      (validate show exists)
           → ShowSeatRepository.findAllByIdInAndStatus(...AVAILABLE)  (check seats free)
           → mark seats BLOCKED, save
           → build Booking (status PENDING), save
       ← Booking object
   ← BookTicketResponse {bookingId, SUCCESS, "please pay"} as JSON
Nothing after this exists yet — no payment confirmation endpoint that flips PENDING → CONFIRMED, no endpoint to browse movies/shows/seat maps, no cancellation.

9. Config
application.properties wires the app to MySQL (spring.datasource.*) and tells Hibernate to auto-sync the schema from the entity classes (ddl-auto=update) and log the SQL it runs (show-sql=true).

In short: the data model (Model layer) is fully fleshed out and mirrors a real booking domain well; the Service/Repository/Controller layers currently implement exactly one slice of that domain — creating a pending booking — and everything else (browsing, payment, cancellation, auth) is unbuilt scaffolding waiting on the same pattern to be repeated.
