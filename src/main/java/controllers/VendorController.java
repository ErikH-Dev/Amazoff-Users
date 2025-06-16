package controllers;

import entities.Vendor;
import interfaces.IVendorService;
import utils.JwtUtil;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Path("/vendors")
@RolesAllowed({"vendor", "admin"})
public class VendorController {
    private static final Logger LOG = Logger.getLogger(VendorController.class);
    private IVendorService vendorService;
    private JwtUtil jwtUtil;

    public VendorController(IVendorService vendorService, JwtUtil jwtUtil) {
        this.vendorService = vendorService;
        this.jwtUtil = jwtUtil;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createVendor() {
        Vendor vendor = new Vendor(
            jwtUtil.getCurrentKeycloakUserId(),
            jwtUtil.getCurrentUserName()
        );

        MDC.put("keycloakId", vendor.getKeycloakId());
        LOG.infof("Received createVendor request for authenticated user: keycloakId=%s, storeName=%s",
                  vendor.getKeycloakId(), vendor.getStoreName());

        return vendorService.create(vendor)
            .onItem().invoke(createdVendor -> LOG.infof("Vendor created: keycloakId=%s", createdVendor.getKeycloakId()))
            .onItem().transform(createdVendor -> Response.status(Response.Status.CREATED).entity(createdVendor).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to create vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("keycloakId");
                return Uni.createFrom().voidItem();
            });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getCurrentVendor() {
        String keycloakId = jwtUtil.getCurrentKeycloakUserId();
        MDC.put("keycloakId", keycloakId);
        LOG.infof("Received getCurrentVendor request for authenticated user: keycloakId=%s", keycloakId);
        
        return vendorService.read(keycloakId)
            .onItem().invoke(vendor -> LOG.infof("Vendor retrieved: keycloakId=%s", vendor.getKeycloakId()))
            .onItem().transform(vendor -> Response.ok(vendor).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to get vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("keycloakId");
                return Uni.createFrom().voidItem();
            });
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateCurrentVendor() {
        Vendor vendor = new Vendor(
            jwtUtil.getCurrentKeycloakUserId(),
            jwtUtil.getCurrentUserName()
        );

        MDC.put("keycloakId", vendor.getKeycloakId());
        LOG.infof("Received updateVendor request for authenticated user: keycloakId=%s", vendor.getKeycloakId());

        return vendorService.update(vendor)
            .onItem().invoke(updatedVendor -> LOG.infof("Vendor updated: keycloakId=%s", updatedVendor.getKeycloakId()))
            .onItem().transform(updatedVendor -> Response.ok(updatedVendor).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to update vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("keycloakId");
                return Uni.createFrom().voidItem();
            });
    }

    @DELETE
    public Uni<Response> deleteCurrentVendor() {
        String keycloakId = jwtUtil.getCurrentKeycloakUserId();
        MDC.put("keycloakId", keycloakId);
        LOG.infof("Received deleteVendor request for authenticated user: keycloakId=%s", keycloakId);
        
        return vendorService.delete(keycloakId)
            .onItem().invoke(v -> LOG.infof("Vendor deleted: keycloakId=%s", keycloakId))
            .onItem().transform(v -> Response.noContent().build())
            .onFailure().invoke(e -> LOG.errorf("Failed to delete vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("keycloakId");
                return Uni.createFrom().voidItem();
            });
    }
}