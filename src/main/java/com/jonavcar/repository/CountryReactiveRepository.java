package com.jonavcar.repository;

import com.jonavcar.models.Country;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CountryReactiveRepository implements PanacheRepository<Country> {
}