# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

A Spring Boot (4.1.0, Java 21) low-level design (LLD) practice project modeling a movie ticket booking platform (BookMyShow-style), backed by MySQL via Spring Data JPA. The codebase is intentionally early-stage/incomplete — entity relationships and one booking flow exist, but most CRUD, auth, and payment logic is not yet built.

## Commands

- Build: `./mvnw clean install`
- Run: `./mvnw spring-boot:run`
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=MovieTicketBookingPlatformApplicationTests`
- Package: `./mvnw clean package`

### Database

The app expects a running MySQL instance:
- URL: `jdbc:mysql://${MYSQL_HOST:localhost}:3306/MovieTicketBooking` (create the `MovieTicketBooking` schema before first run)
- Username: `root`, password from `spring.datasource.password` in `src/main/resources/application.properties` (currently blank locally — do not commit real credentials here)
- `spring.jpa.hibernate.ddl-auto=update` — schema is auto-migrated from entities on startup, no separate migration tool
- `spring.jpa.show-sql=true` — SQL is logged to stdout

Note the compiler is configured with `--enable-preview` (see `pom.xml` maven-compiler-plugin config), so preview Java 21 features may be used/expected to compile.

## Architecture

Standard layered Spring Boot structure under `com.backendlld.movieticketbookingplatform`:

- `model/` — JPA entities. All extend `BaseModel` (`id`, `createdAt`, `updatedAt`) via `@MappedSuperclass`.
- `model/Enums/` — persisted enums (`BookingStatus`, `PaymentStatus`, `PaymentMode`, `ShowSeatStatus`, `Languages`, `Features`).
- `repository/` — Spring Data `JpaRepository` interfaces, one per aggregate that needs queries (`ShowRepository`, `UserRepository`, `BookingRepository`, `ShowSeatRepository`).
- `service/` — business logic (currently only `BookingService`).
- `controller/` — currently plain `@Controller` classes calling services directly (not yet `@RestController`/REST-mapped; `BookingController.bookTicket` has no `@RequestMapping` wired up yet).
- `dtos/` — request/response shapes for controllers (e.g. `BookTicketRequest`/`BookTicketResponse`), decoupled from entities.

### Domain model shape

The core entity graph, useful when adding new features:

- `Theatre` → has many `Screen`s (via `@OneToMany` + `@JoinColumn(theatre_id)`) and a `@ManyToMany` to `Movie`; belongs to a `Region`.
- `Screen` → has many `Seat`s; has `Features` (2D/3D/4DX) as an `@ElementCollection`.
- `Movie` → has `Actor`s, `Languages`, `Features` as collections.
- `Show` → ties a `Movie` + `Screen` + `Theatre` + `date` together; has many `ShowSeat`s (per-show instance of a physical `Seat`, carrying availability `ShowSeatStatus`) and many `ShowSeatType`s (per-show pricing for a `SeatType`).
- `Seat` → physical seat (`row`, `column`, `seatName`), tied to a `SeatType` (e.g. Regular/Premium).
- `Booking` → made by a `User`, references the `ShowSeat`s booked (`@ManyToMany`, deliberately not `@OneToMany`, to allow a seat to appear across multiple bookings over time as cancellations/rebooking are supported later) and has many `Payment`s (supports partial/retried payments), tracked via `BookingStatus`.
- `Payment` → belongs to one `Booking`, tracks `PaymentStatus`/`PaymentMode`.

### Booking flow (`BookingService.bookTicket`)

Reference flow for how a booking is expected to be built end-to-end:
1. Look up `User` by id; if not found, currently auto-creates a placeholder user (a `// throw new RuntimeException("User not found")` is left commented out as the alternative — follow existing behavior unless asked to change it).
2. Look up `Show` by id; throws if missing.
3. Fetch requested `ShowSeat`s filtered to `ShowSeatStatus.AVAILABLE`; throws if any requested seat isn't available.
4. Marks those seats `BLOCKED` and persists them.
5. Creates the `Booking` in `PENDING` status.
6. Pricing is stubbed (`totalAmount` hardcoded to `100`) — real pricing via `ShowSeatType` is a known TODO (see comment `// implement logic to get total price of booking`).

When extending this flow (payments, cancellations, seat release on timeout), follow this same repository-mediated, exception-on-invalid-state pattern rather than introducing a different style.

## Conventions in this codebase

- Lombok `@Getter`/`@Setter` on entities/DTOs instead of manual accessors — keep using it for new classes.
- Constructor injection with `@Autowired` on the constructor (not field injection) for services/controllers.
- No global exception handling yet — services throw plain `RuntimeException` with a message; controllers catch `Exception` and map to a `ResponseStatus` enum (`SUCCESS`/`FAILURE`) in the response DTO.