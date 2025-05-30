package com.jonavcar;

import com.jonavcar.models.CountResponse;
import com.jonavcar.services.SearchService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/ping-virtual")
public class PingVirtualResource {

    @Inject
    SearchService searchService;

    @GET
    @RunOnVirtualThread
    public CountResponse ping() {
        long c = searchService.countHiloOccurrences();
        return new CountResponse(c);
    }

}
