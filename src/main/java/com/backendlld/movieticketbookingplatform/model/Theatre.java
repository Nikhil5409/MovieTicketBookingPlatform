package com.backendlld.movieticketbookingplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Theatre extends BaseModel {
    private String name;
    private String address;
    @ManyToOne
    private Region region;
    private double rating;
    @OneToMany
    private List<Screen> screens;
    @ManyToMany
    private List<Movie> movies;
}
