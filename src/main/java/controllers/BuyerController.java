package controllers;

import dtos.CreateBuyerRequest;
import dtos.UpdateBuyerRequest;
import interfaces.IBuyerService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Path("/buyers")
public class BuyerController {
    private static final Logger LOG = Logger.getLogger(BuyerController.class);
    private IBuyerService buyerService;

    public BuyerController(IBuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addBuyer(@Valid CreateBuyerRequest buyerRequest) {
        LOG.infof("Received addBuyer request: oauthId=%d, email=%s", buyerRequest.oauthId, buyerRequest.email);
        return buyerService.create(buyerRequest)
            .onItem().invoke(buyer -> {
                MDC.put("buyerId", buyer.getOauthId());
                LOG.infof("Buyer created: oauthId=%d", buyer.getOauthId());
                MDC.remove("buyerId");
            })
            .onItem().transform(createdBuyer -> Response.ok(createdBuyer).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to create buyer: %s", e.getMessage()));
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getBuyerById(@PathParam("id") int oauthId) {
        MDC.put("buyerId", oauthId);
        LOG.infof("Received getBuyerById request: oauthId=%d", oauthId);
        return buyerService.read(oauthId)
            .onItem().invoke(buyer -> LOG.infof("Buyer retrieved: oauthId=%d", buyer.getOauthId()))
            .onItem().transform(buyer -> Response.ok(buyer).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to get buyer: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("buyerId");
                return Uni.createFrom().voidItem();
            });
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateBuyer(@Valid UpdateBuyerRequest buyerRequest) {
        MDC.put("buyerId", buyerRequest.oauthId);
        LOG.infof("Received updateBuyer request: oauthId=%d", buyerRequest.oauthId);
        return buyerService.update(buyerRequest)
            .onItem().invoke(updatedBuyer -> LOG.infof("Buyer updated: oauthId=%d", updatedBuyer.getOauthId()))
            .onItem().transform(updatedBuyer -> Response.ok(updatedBuyer).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to update buyer: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("buyerId");
                return Uni.createFrom().voidItem();
            });
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteBuyer(@PathParam("id") int oauthId) {
        MDC.put("buyerId", oauthId);
        LOG.infof("Received deleteBuyer request: oauthId=%d", oauthId);
        return buyerService.delete(oauthId)
            .onItem().invoke(v -> LOG.infof("Buyer deleted: oauthId=%d", oauthId))
            .onItem().transform(v -> Response.noContent().build())
            .onFailure().invoke(e -> LOG.errorf("Failed to delete buyer: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("buyerId");
                return Uni.createFrom().voidItem();
            });
    }
}