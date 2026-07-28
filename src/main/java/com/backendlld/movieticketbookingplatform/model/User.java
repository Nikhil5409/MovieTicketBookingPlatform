package com.backendlld.movieticketbookingplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class User extends BaseModel {
    private String username;
    private String email;
    @OneToMany(mappedBy = "bookedBy")
    private List<Booking> bookings;
}
