package com.backendlld.movieticketbookingplatform.repository;

import com.backendlld.movieticketbookingplatform.model.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatType, Long> {
}