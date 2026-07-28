package com.backendlld.movieticketbookingplatform.model;

import com.backendlld.movieticketbookingplatform.model.Enums.Features;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Screen extends BaseModel{
    private String name;
    @OneToMany
    @JoinColumn(name="screen_id")
    private List<Seat> seats;
    @Enumerated
    @ElementCollection
    private List<Features> screenFeatures;


}
