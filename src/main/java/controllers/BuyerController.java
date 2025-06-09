package controllers;

import entities.Buyer;
import interfaces.IBuyerService;
import utils.JwtUtil;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Path("/buyers")
@RolesAllowed({ "buyer", "admin" })
public class BuyerController {
    private static final Logger LOG = Logger.getLogger(BuyerController.class);
    private IBuyerService buyerService;
    private JwtUtil jwtUtil;

    public BuyerController(IBuyerService buyerService, JwtUtil jwtUtil) {
        this.buyerService = buyerService;
        this.jwtUtil = jwtUtil;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createBuyer() {
        Buyer buyer = new Buyer(
                jwtUtil.getCurrentKeycloakUserId(),
                jwtUtil.getCurrentUserFirstName(),
                jwtUtil.getCurrentUserLastName(),
                jwtUtil.getCurrentUserEmail());

        MDC.put("keycloakId", buyer.getKeycloakId());
        LOG.infof("Received createBuyer request for authenticated user: keycloakId=%s", buyer.getKeycloakId());

        return buyerService.create(buyer)
                .onItem().invoke(created -> {
                    MDC.put("buyerId", created.getKeycloakId());
                    LOG.infof("Buyer created: keycloakId=%s", created.getKeycloakId());
                    MDC.remove("buyerId");
                })
                .onItem().transform(createdBuyer -> Response.ok(createdBuyer).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to create buyer: %s", e.getMessage()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getCurrentBuyer() {
        String keycloakId = jwtUtil.getCurrentKeycloakUserId();
        MDC.put("keycloakId", keycloakId);
        LOG.infof("Received getCurrentBuyer request for authenticated user: keycloakId=%s", keycloakId);

        return buyerService.read(keycloakId)
                .onItem().invoke(buyer -> LOG.infof("Buyer retrieved: keycloakId=%s", buyer.getKeycloakId()))
                .onItem().transform(buyer -> Response.ok(buyer).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to get buyer: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("keycloakId");
                    return Uni.createFrom().voidItem();
                });
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateCurrentBuyer() {
        Buyer buyer = new Buyer(
                jwtUtil.getCurrentKeycloakUserId(),
                jwtUtil.getCurrentUserFirstName(),
                jwtUtil.getCurrentUserLastName(),
                jwtUtil.getCurrentUserEmail());

        MDC.put("keycloakId", buyer.getKeycloakId());
        LOG.infof("Received updateBuyer request for authenticated user: keycloakId=%s", buyer.getKeycloakId());

        return buyerService.update(buyer)
                .onItem()
                .invoke(updatedBuyer -> LOG.infof("Buyer updated: keycloakId=%s", updatedBuyer.getKeycloakId()))
                .onItem().transform(updatedBuyer -> Response.ok(updatedBuyer).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to update buyer: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("keycloakId");
                    return Uni.createFrom().voidItem();
                });
    }

    @DELETE
    public Uni<Response> deleteCurrentBuyer() {
        String keycloakId = jwtUtil.getCurrentKeycloakUserId();
        MDC.put("keycloakId", keycloakId);
        LOG.infof("Received deleteBuyer request for authenticated user: keycloakId=%s", keycloakId);

        return buyerService.delete(keycloakId)
                .onItem().invoke(v -> LOG.infof("Buyer deleted: keycloakId=%s", keycloakId))
                .onItem().transform(v -> Response.noContent().build())
                .onFailure().invoke(e -> LOG.errorf("Failed to delete buyer: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("keycloakId");
                    return Uni.createFrom().voidItem();
                });
    }
}