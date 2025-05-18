package com.jonavcar.threads;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.*;

@Path("/threads/custom")
@ApplicationScoped
public class CustomPoolResource extends AbstractExecutorResource {
    @ConfigProperty(name = "threads.custom.pool-size", defaultValue = "10")
    int poolSize;

    @ConfigProperty(name = "threads.custom.queue-size", defaultValue = "100")
    int queueSize;

    @Override
    protected ExecutorService createExecutor() {
        // Fijo con cola acotada para igualdad de condiciones
        return new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize)
        );
    }

    @GET
    public CompletionStage<String> customPool() {
        return executeTask(() -> blockingOperation("custom"));
    }
}