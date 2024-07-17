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
    // http://localhost:8083/apikey/key
    @Path("/key")
    @Produces(MediaType.TEXT_PLAIN)
    Response getClientByApiKey(@HeaderParam("apikey") String apikey);


}