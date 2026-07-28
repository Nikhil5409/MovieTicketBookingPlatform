package com.backendlld.movieticketbookingplatform.repository;

import com.backendlld.movieticketbookingplatform.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
}