package com.example.client;

import com.example.model.MessageDTO;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterProvider(ProblemResponseExceptionMapper.class)
@RegisterRestClient
public interface ServiceClient {

  @Path("/greeting")
  @GET
  MessageDTO greet() throws ThrowableProblem;
}
