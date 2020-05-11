package com.example.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class ProblemResponseExceptionMapper implements ResponseExceptionMapper<ThrowableProblem> {

  private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new ProblemModule())
    .registerModule(new KotlinModule());

  @Override
  public ThrowableProblem toThrowable(Response response) {
    var content = response.readEntity(String.class);
    try {
      return MAPPER.readValue(content, ThrowableProblem.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("could not read ThrowableProblem: " + content, e);
    }
  }

  @Override
  public boolean handles(int status, MultivaluedMap<String, Object> headers) {
    return headers.containsKey("x-custom-problem");
  }
}
