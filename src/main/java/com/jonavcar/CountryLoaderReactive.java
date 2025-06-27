package com.jonavcar;

import com.jonavcar.models.Country;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Startup
@ApplicationScoped
public class CountryLoaderReactive {

    @PostConstruct
    void init() {
        cargarPaises()
                .subscribe().with(
                        x -> System.out.println("Carga de países completada."),
                        Throwable::printStackTrace
                );
    }

    Uni<Void> cargarPaises() {
        return Country.deleteAll()
                .replaceWith(() -> {
                    InputStream in = getClass().getResourceAsStream("/countries.csv");
                    if (in == null) throw new RuntimeException("Archivo countries.csv no encontrado");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        String line;
                        boolean first = true;
                        Uni<Void> chain = Uni.createFrom().voidItem();
                        while ((line = reader.readLine()) != null) {
                            if (first) {
                                first = false;
                                continue;
                            }
                            String[] fields = line.split(";");
                            if (fields.length < 4) continue;
                            Country c = new Country();
                            c.iso2 = fields[0];
                            c.iso3 = fields[1];
                            c.name = fields[2];
                            c.continent = fields[3];
                            c.capital = fields.length > 4 ? fields[4] : "";
                            chain = chain.chain(() -> c.persist());
                        }
                        return chain;
                    } catch (Exception e) {
                        return Uni.createFrom().failure(e);
                    }
                });
    }
}