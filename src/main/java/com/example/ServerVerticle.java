package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import java.time.Instant;

public final class ServerVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    vertx
        .createHttpServer()
        .requestHandler(
            event -> {
              System.out.println(Instant.now() + " Processing request.");
              vertx.executeBlocking(
                  () -> {
                    try {
                      Thread.sleep(5000);
                      System.out.println(Instant.now() + " Processed request.");
                      event.response().end("Hello");
                    } catch (InterruptedException e) {
                      System.err.println(Instant.now() + " Processing interrupted.");
                      event.response().end("Failed to process.");
                    }
                    return null;
                  },
                  false);
            })
        .listen(8080)
        .onSuccess(event -> startPromise.complete())
        .onFailure(startPromise::fail);
  }
}
