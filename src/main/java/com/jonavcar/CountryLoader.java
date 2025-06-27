package com.jonavcar;

import com.jonavcar.models.Country;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Startup
@ApplicationScoped
public class CountryLoader {

    @PostConstruct
    void init() {
        loadCountries();
    }

    @Transactional
    void loadCountries() {
        Country.deleteAll();

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
                if (fields.length < 4) {
                    System.err.println("Línea inválida (faltan columnas): " + line);
                    continue;
                }
                Country country = new Country();
                country.iso2 = fields[0];
                country.iso3 = fields[1];
                country.name = fields[2];
                country.continent = fields[3];
                country.capital = fields.length > 4 ? fields[4] : "";
                country.persist();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}