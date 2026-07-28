package com.backendlld.movieticketbookingplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MovieTicketBookingPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieTicketBookingPlatformApplication.class, args);
    }
}

