package controllers;

import dtos.CreateBuyerRequest;
import dtos.UpdateBuyerRequest;
import interfaces.IBuyerService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/buyers")
public class BuyerController {
    private IBuyerService buyerService;

    public BuyerController(IBuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addBuyer(@Valid CreateBuyerRequest buyerRequest) {
        return buyerService.create(buyerRequest)
            .onItem().transform(createdBuyer -> Response.ok(createdBuyer).build());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getBuyerById(@PathParam("id") int oauthId) {
        return buyerService.read(oauthId)
            .onItem().transform(buyer -> Response.ok(buyer).build());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateBuyer(@Valid UpdateBuyerRequest buyerRequest) {
        return buyerService.update(buyerRequest)
            .onItem().transform(updatedBuyer -> Response.ok(updatedBuyer).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteBuyer(@PathParam("id") int oauthId) {
        return buyerService.delete(oauthId)
            .onItem().transform(v -> Response.noContent().build());
    }
}