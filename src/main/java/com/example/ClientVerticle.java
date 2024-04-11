package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import java.time.Instant;

public final class ClientVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    final WebClient webClient = WebClient.create(vertx);
    System.out.println(Instant.now() + " Sending http request.");
    webClient
        .getAbs("http://localhost:8080")
        .as(BodyCodec.string())
        .send()
        .onSuccess(
            response -> {
              System.out.println(
                  Instant.now()
                      + " Response status code: "
                      + response.statusCode()
                      + " body: "
                      + response.body());
              startPromise.complete();
            })
        .onFailure(
            event -> {
              System.err.println(Instant.now() + " HTTP request failed: " + event);
              startPromise.fail(event);
            });
  }
}
