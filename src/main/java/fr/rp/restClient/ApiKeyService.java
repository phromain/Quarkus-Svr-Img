package fr.rp.restClient;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@Path("/apikey")
@RegisterRestClient(baseUri = "ApiKeyService")
@ApplicationScoped
public interface ApiKeyService {

    @GET
    // http://localhost:8083/apikey/{apikey}
    @Path("/{apikey}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getClientByApiKey(@PathParam("apikey") String apikey);

}