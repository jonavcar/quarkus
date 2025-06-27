package com.jonavcar;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/util")
public class UtilResource {

    @Inject
    CountryLoader countryLoader;

    @GET
    @Path("/load")
    public Response loadCountries() {
        countryLoader.loadCountries();
        return Response.ok("Países cargados correctamente").build();
    }
}