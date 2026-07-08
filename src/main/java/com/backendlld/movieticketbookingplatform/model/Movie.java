package com.backendlld.movieticketbookingplatform.model;

import com.backendlld.movieticketbookingplatform.model.Enums.Features;
import com.backendlld.movieticketbookingplatform.model.Enums.Languages;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Movie extends BaseModel{
    private String title;
    private String director;
    private String year;
    private String genre;
    private double rating;
    @ManyToMany
    private List<Actor> actors;
    @Enumerated
    @ElementCollection
    private List<Languages> languages;
    @Enumerated
    @ElementCollection
    private List<Features> features;
}
