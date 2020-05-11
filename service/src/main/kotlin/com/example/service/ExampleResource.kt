package com.example.service

import com.example.model.MessageDTO
import org.eclipse.microprofile.context.ManagedExecutor
import java.util.concurrent.CompletionStage
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path(ExampleResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class ExampleResource @Inject constructor(private val executor: ManagedExecutor) {

    @GET
    fun greet(@QueryParam("name") name: String?): CompletionStage<Response> {
        return executor.supplyAsync {
            val who = name ?: "World"
            MessageDTO("Hello ${who}!")
        }.thenApply { dto ->
            Response.ok(dto).build()
        }
    }

    companion object {
        const val PATH = "/greet"
    }
}
