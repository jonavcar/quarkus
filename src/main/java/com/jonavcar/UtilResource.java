package com.jonavcar;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/util")
@Produces(MediaType.TEXT_PLAIN)
public class UtilResource {

    @Inject
    CountryLoader loader;

    @GET
    @Path("/load")
    public Uni<String> load() {
        return loader.cargarPaises().replaceWith("OK");
    }
}