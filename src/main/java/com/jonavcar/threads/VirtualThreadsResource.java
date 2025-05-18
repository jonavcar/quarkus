package com.jonavcar.threads;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/threads/virtual")
@ApplicationScoped
public class VirtualThreadsResource extends AbstractExecutorResource {
    @Override
    protected ExecutorService createExecutor() {
        // Executor de virtual threads (Java 21+)
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @GET
    public CompletionStage<String> virtualThreads() {
        return executeTask(() -> blockingOperation("virtual"));
    }
}