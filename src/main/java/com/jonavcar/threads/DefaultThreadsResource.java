package com.jonavcar.threads;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.concurrent.ExecutorService;

@Path("/threads/default")
@ApplicationScoped
public class DefaultThreadsResource extends AbstractExecutorResource {
    @Override
    protected ExecutorService createExecutor() {
        // Synchronous: no executor, se bloquea en el hilo worker
        return null;
    }

    @GET
    public String defaultThreads() {
        return blockingOperation("default");
    }
}