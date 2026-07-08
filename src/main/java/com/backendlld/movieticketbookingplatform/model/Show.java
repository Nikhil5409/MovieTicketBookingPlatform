package com.backendlld.movieticketbookingplatform.model;

import com.backendlld.movieticketbookingplatform.model.Enums.Features;
import com.backendlld.movieticketbookingplatform.model.Enums.Languages;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Show extends BaseModel{
    @ManyToOne
    private Movie movie;
    @ManyToOne
    private Screen screen;
    @ManyToOne
    private Theatre theatre;
    private Date date;
    @OneToMany
    private List<ShowSeat> showSeats;
    @OneToMany
    private List<ShowSeatType> showSeatTypes;
    @Enumerated
    private Languages language;
    @Enumerated
    @ElementCollection
    private List<Features> features;
}
