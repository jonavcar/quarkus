package com.jonavcar;

import com.jonavcar.models.Country;
import com.jonavcar.repository.CountryReactiveRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CountryLoader {

    @Inject
    CountryReactiveRepository countryRepository;

    @WithTransaction
    public Uni<Void> cargarPaises() {
        return countryRepository.deleteAll()
                .chain(this::leerPaisesDesdeCSV)
                .chain(countries -> countryRepository.persist(countries));
    }

    private Uni<List<Country>> leerPaisesDesdeCSV() {
        return Uni.createFrom().item(() -> {
            List<Country> countries = new ArrayList<>();
            InputStream in = getClass().getResourceAsStream("/countries.csv");
            if (in == null) {
                throw new RuntimeException("Archivo countries.csv no encontrado en resources");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] fields = line.split(";");
                    if (fields.length < 4) continue;
                    Country country = new Country();
                    country.iso2 = fields[0];
                    country.iso3 = fields[1];
                    country.name = fields[2];
                    country.continent = fields[3];
                    country.capital = fields.length > 4 ? fields[4] : "";
                    countries.add(country);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return countries;
        });
    }
}