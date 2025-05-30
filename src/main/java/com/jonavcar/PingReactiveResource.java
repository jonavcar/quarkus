package com.jonavcar;

import com.jonavcar.models.CountResponse;
import com.jonavcar.services.SearchService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/ping-reactive")
public class PingReactiveResource {

    @Inject
    SearchService searchService;

    @GET
    public Uni<CountResponse> ping() {
        return Uni.createFrom().item(() -> {
                    long c = searchService.countHiloOccurrences();
                    return new CountResponse(c);
                })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor());
    }
}
