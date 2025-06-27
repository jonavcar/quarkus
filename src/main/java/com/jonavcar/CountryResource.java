package com.jonavcar;

import com.jonavcar.models.Country;
import com.jonavcar.repository.CountryReactiveRepository;
import com.jonavcar.services.SearchService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/countries")
@Produces(MediaType.APPLICATION_JSON)
public class CountryResource {

    @Inject
    CountryReactiveRepository countryReactiveRepository;

    @Inject
    SearchService searchService;

    @GET
    @Path("/imperative")
    public Response countImperative() {
        List<Country> list = countryReactiveRepository.listAll().await().indefinitely();
        int countCountry = list.size();
        long countSearch = searchService.searchCount();
        return Response.ok(Map.of(
                "CountCountry", countCountry,
                "CountSearch", countSearch
        )).build();
    }

    @GET
    @Path("/ping-reactive")
    public Uni<Response> reactive() {
        return countryReactiveRepository.listAll()
                .flatMap(countries ->
                        Uni.createFrom().item(() -> searchService.searchCount())
                                .map(count -> Response.ok(Map.of(
                                        "CountCountry", countries.size(),
                                        "CountSearch", count
                                )).build())
                );
    }

    @GET
    @Path("/virtual")
    @RunOnVirtualThread
    public Response countVirtual() {
        List<Country> list = countryReactiveRepository.listAll().await().indefinitely();
        int countCountry = list.size();
        long countSearch = searchService.searchCount();
        return Response.ok(Map.of(
                "CountCountry", countCountry,
                "CountSearch", countSearch
        )).build();
    }
}