package com.example;

import com.example.client.ServiceClient;
import io.quarkus.runtime.QuarkusApplication;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;

public class HelloWorldService implements QuarkusApplication {

  private static final Logger LOGGER = Logger.getLogger(HelloWorldService.class);

  private final ServiceClient client;

  @Inject
  public HelloWorldService(@RestClient ServiceClient client) {
    this.client = client;
  }

  @Override
  public int run(String... args) {
    System.out.println("calling service");
    LOGGER.info("calling service...");
    var message = client.greet();
    LOGGER.info(message.getContent());

    return 0;
  }
}
