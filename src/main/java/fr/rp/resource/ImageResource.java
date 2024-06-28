package fr.rp.resource;

import fr.rp.restClient.ApiKeyService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.coobird.thumbnailator.Thumbnails;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Path("/images")
public class ImageResource {

    @RestClient
    ApiKeyService apiKeyService;

    @GET
    @Path("/{name}/{size}")
    @Produces({"image/jpeg",MediaType.TEXT_PLAIN})
    @Operation(summary = "Retourne l'image à afficher", description = "Retourne l'image à afficher")
    @APIResponse(responseCode = "200", description = "Image trouvée")
    @APIResponse(responseCode = "400", description = "Format non valide")
    @APIResponse(responseCode = "404", description = "Image non trouvée")
    @APIResponse(responseCode = "500", description = "Une erreur est survenue")
    public Response getImage(@PathParam("name") String name, @PathParam("size") String size) {
        try {
            java.nio.file.Path imagePath = java.nio.file.Paths.get("src/images", name + ".jpg");

            if (!java.nio.file.Files.exists(imagePath)) {
                return Response.status(Response.Status.NOT_FOUND).entity("Image non trouvée").build();
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            int width, height;
            switch (size) {
                case "sm":
                    width = 250;
                    height = 250;
                    break;
                case "md":
                    width = 300;
                    height = 300;
                    break;
                case "lg":
                    width = 1080;
                    height = 1080;
                    break;
                case "nm":
                    BufferedImage bimg = ImageIO.read(imagePath.toFile());
                    width = bimg.getWidth();
                    height = bimg.getHeight();
                    break;
                default:
                    return Response.status(Response.Status.BAD_REQUEST).entity("Taille non valide").build();
            }

            Thumbnails.of(imagePath.toFile()).size(width, height).toOutputStream(os);
            return Response.ok(os.toByteArray()).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Une erreur est survenue").build();
        }
    }



    @POST
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes({"image/png","image/jpeg", "image/gif"})
    @Operation(summary = "Enregistre l image dans un format générique (Jpeg)", description = "Enregistre l image dans un format générique (Jpeg)")
    @APIResponse(responseCode = "201", description = "Image enregistré")
    @APIResponse(responseCode = "400", description = "Extension non prise en compte (uniquement JPEG, PNG et GIF")
    @APIResponse(responseCode = "401", description = "Parametre Authentification manquant")
    @APIResponse(responseCode = "404", description = "API-KEY non trouvé")
    @APIResponse(responseCode = "500", description = "Une erreur est survenue")
    public Response createImage(@PathParam("name") String name, byte[] imageBytes, @HeaderParam("Content-Type") String contentType, @HeaderParam("API-KEY") String apiKey ) {
        try {
            if (apiKey == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Parametre Authentification manquant")
                        .build();
            }
            if (apiKeyService.getClientByApiKey(apiKey).getStatus() != Response.Status.OK.getStatusCode()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("API-KEY non trouvé")
                        .build();
            }
            if (!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("image/gif")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Extension non prise en compte (uniquement JPEG, PNG et GIF").build();
            }
            java.nio.file.Path imagePath = java.nio.file.Paths.get("src/images", name + ".jpg");
            Files.write((java.nio.file.Path) imagePath, imageBytes);
            return Response.ok("Image enregistré").build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Une erreur est survenue").build();
        }
    }


    @PUT
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes({"image/png","image/jpeg", "image/gif"})
    @Operation(summary = "Met à jour l'image", description = "Met à jour l'image")
    @APIResponse(responseCode = "200", description = "Image mise à jour ")
    @APIResponse(responseCode = "400", description = "Extension non prise en compte (uniquement JPEG, PNG et GIF")
    @APIResponse(responseCode = "401", description = "Parametre Authentification manquant")
    @APIResponse(responseCode = "404", description = "API-KEY non trouvé")
    @APIResponse(responseCode = "404", description = "Image non trouvée")
    @APIResponse(responseCode = "500", description = "Une erreur est survenue")
    public Response updateImage(@PathParam("name") String name, byte[] imageBytes, @HeaderParam("Content-Type") String contentType, @HeaderParam("API-KEY") String apiKey ) {
        try {
            if (apiKey == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Parametre Authentification manquant")
                        .build();
            }
            if (apiKeyService.getClientByApiKey(apiKey).getStatus() != Response.Status.OK.getStatusCode()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("API-KEY non trouvé")
                        .build();
            }
            if (!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("image/gif")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Extension non prise en compte (uniquement JPEG, PNG et GIF").build();
            }

            java.nio.file.Path imagePath = java.nio.file.Paths.get("src/images", name + ".jpg");

            if (!java.nio.file.Files.exists(imagePath)) {
                return Response.status(Response.Status.NOT_FOUND).entity("Image non trouvée").build();
            }

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ImageIO.write(img, "jpg", imagePath.toFile());

            return Response.ok("Image mise à jour ").build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Une erreur est survenue").build();
        }
    }


    @DELETE
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Supprime l'image", description = "Supprime l'image")
    @APIResponse(responseCode = "200", description = "Image supprimée")
    @APIResponse(responseCode = "401", description = "Parametre Authentification manquant")
    @APIResponse(responseCode = "404", description = "API-KEY non trouvé")
    @APIResponse(responseCode = "404", description = "Image non trouvée")
    @APIResponse(responseCode = "500", description = "Une erreur est survenue")
    public Response deleteImage(@PathParam("name") String name, @HeaderParam("API-KEY") String apiKey ) {
        try {
            if (apiKey == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Parametre Authentification manquant")
                        .build();
            }
            if (apiKeyService.getClientByApiKey(apiKey).getStatus() != Response.Status.OK.getStatusCode()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("API-KEY non trouvé")
                        .build();
            }

            java.nio.file.Path imagePath = java.nio.file.Paths.get("src/images", name + ".jpg");

            if (!java.nio.file.Files.exists(imagePath)) {
                return Response.status(Response.Status.NOT_FOUND).entity("Image non trouvée").build();
            }

            java.nio.file.Files.delete(imagePath);

            return Response.ok("Image supprimée").build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Une erreur est survenue").build();
        }
    }

    }
