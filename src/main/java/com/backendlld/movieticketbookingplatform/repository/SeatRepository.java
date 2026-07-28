package com.backendlld.movieticketbookingplatform.repository;

import com.backendlld.movieticketbookingplatform.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}