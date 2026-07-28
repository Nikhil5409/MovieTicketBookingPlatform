package com.backendlld.movieticketbookingplatform.repository;

import com.backendlld.movieticketbookingplatform.model.Enums.ShowSeatStatus;
import com.backendlld.movieticketbookingplatform.model.ShowSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ShowSeat> findAllByIdInAndStatus(Iterable<Long> ids, ShowSeatStatus status);
}
