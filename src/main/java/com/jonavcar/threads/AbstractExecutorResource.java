package com.jonavcar.threads;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * Base class that manages lifecycle of an ExecutorService
 * and provides helper methods for simulated blocking tasks.
 */
public abstract class AbstractExecutorResource {
    protected ExecutorService pool;

    @PostConstruct
    void init() {
        pool = createExecutor();
    }

    @PreDestroy
    void shutdown() {
        if (pool != null) {
            pool.shutdown();
        }
    }

    /**
     * Subclasses provide the ExecutorService (or null for sync).
     */
    protected abstract ExecutorService createExecutor();

    /**
     * Simulates a 100 ms blocking operation.
     */
    protected String blockingOperation(String tag) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Thread.currentThread().getName() + ":ok-" + tag;
    }

    /**
     * Executes supplier async if pool exists, else sync.
     */
    protected CompletionStage<String> executeTask(Supplier<String> supplier) {
        if (pool != null) {
            return CompletableFuture.supplyAsync(supplier, pool);
        } else {
            return CompletableFuture.completedFuture(supplier.get());
        }
    }
}