package com.backendlld.movieticketbookingplatform.repository;

import com.backendlld.movieticketbookingplatform.model.Enums.ShowSeatStatus;
import com.backendlld.movieticketbookingplatform.model.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    List<ShowSeat> findAllByIdInAndStatus(Iterable<Long> ids, ShowSeatStatus status);
}
