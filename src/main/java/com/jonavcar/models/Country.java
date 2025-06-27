package com.jonavcar.models;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Country extends PanacheEntity {
    public String iso2;
    public String iso3;
    public String name;
    public String continent;
    public String capital;
}