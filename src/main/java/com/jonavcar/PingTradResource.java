package com.jonavcar;

import com.jonavcar.models.CountResponse;
import com.jonavcar.services.SearchService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/ping-trad")
public class PingTradResource {

    @Inject
    SearchService searchService;

    @GET
    public CountResponse ping() {
        long c = searchService.countHiloOccurrences();
        return new CountResponse(c);
    }
}
