package com.jonavcar.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;


@ApplicationScoped
public class SearchService {

    private static final Pattern WORD = Pattern.compile("\\bhilo\\b", Pattern.CASE_INSENSITIVE);

    @ConfigProperty(name = "hilo.resource-name", defaultValue = "file.txt")
    String resourceName;

    public long countHiloOccurrences() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream is = cl.getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IllegalStateException("Recurso no encontrado en classpath: " + resourceName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines()
                        .mapToLong(line -> {
                            var m = WORD.matcher(line);
                            long c = 0;
                            while (m.find()) c++;
                            return c;
                        })
                        .sum();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}