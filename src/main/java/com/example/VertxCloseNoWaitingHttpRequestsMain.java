package com.example;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public final class VertxCloseNoWaitingHttpRequestsMain {

  // Helps to wait for a vertx Future.
  private static <T> void await(Future<T> future) {
    future.toCompletionStage().toCompletableFuture().join();
  }

  public static void main(String[] args) throws InterruptedException {
    final Vertx serverVertx = Vertx.vertx();
    final Vertx clientVertx = Vertx.vertx();

    // Wait for server to be ready.
    System.out.println(Instant.now() + " Deploying server");
    await(serverVertx.deployVerticle(ServerVerticle.class, new DeploymentOptions()));
    // Start clients when server is ready.
    System.out.println(Instant.now() + " Deploying client");
    clientVertx.deployVerticle(ClientVerticle.class, new DeploymentOptions());

    final AtomicReference<Future<Void>> serverCloseFutureRef = new AtomicReference<>();

    // The server is taking 5s to process. Vertx is set to close in the middle of the processing.
    System.out.println(Instant.now() + " Setting timer to close server vertx");
    clientVertx.setTimer(
        2000,
        event -> {
          System.out.println(Instant.now() + " Closing server vertx");
          serverCloseFutureRef.set(serverVertx.close());
        });

    // Wait for ample time.
    Thread.sleep(10000);

    // Wait for server vertx shutdown.
    final Future<Void> serverCloseFuture = serverCloseFutureRef.get();
    if (serverCloseFuture != null) {
      await(serverCloseFuture);
    }

    // Wait for client vertx shutdown.
    await(clientVertx.close());
  }
}
