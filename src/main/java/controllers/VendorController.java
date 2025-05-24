package controllers;

import dtos.CreateVendorRequest;
import dtos.UpdateVendorRequest;
import interfaces.IVendorService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Path("/vendors")
public class VendorController {
    private static final Logger LOG = Logger.getLogger(VendorController.class);
    private IVendorService vendorService;

    public VendorController(IVendorService vendorService) {
        this.vendorService = vendorService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addVendor(@Valid CreateVendorRequest vendorRequest) {
        LOG.infof("Received addVendor request: oauthId=%d, storeName=%s", vendorRequest.oauthId, vendorRequest.storeName);
        return vendorService.create(vendorRequest)
            .onItem().invoke(vendor -> {
                MDC.put("vendorId", vendor.getOauthId());
                LOG.infof("Vendor created: oauthId=%d", vendor.getOauthId());
                MDC.remove("vendorId");
            })
            .onItem().transform(createdVendor -> Response.ok(createdVendor).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to create vendor: %s", e.getMessage()));
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getVendorById(@PathParam("id") int oauthId) {
        MDC.put("vendorId", oauthId);
        LOG.infof("Received getVendorById request: oauthId=%d", oauthId);
        return vendorService.read(oauthId)
            .onItem().invoke(vendor -> LOG.infof("Vendor retrieved: oauthId=%d", vendor.getOauthId()))
            .onItem().transform(vendor -> Response.ok(vendor).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to get vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("vendorId");
                return Uni.createFrom().voidItem();
            });
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateVendor(@Valid UpdateVendorRequest vendorRequest) {
        MDC.put("vendorId", vendorRequest.oauthId);
        LOG.infof("Received updateVendor request: oauthId=%d", vendorRequest.oauthId);
        return vendorService.update(vendorRequest)
            .onItem().invoke(updatedVendor -> LOG.infof("Vendor updated: oauthId=%d", updatedVendor.getOauthId()))
            .onItem().transform(updatedVendor -> Response.ok(updatedVendor).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to update vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("vendorId");
                return Uni.createFrom().voidItem();
            });
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteVendor(@PathParam("id") int oauthId) {
        MDC.put("vendorId", oauthId);
        LOG.infof("Received deleteVendor request: oauthId=%d", oauthId);
        return vendorService.delete(oauthId)
            .onItem().invoke(v -> LOG.infof("Vendor deleted: oauthId=%d", oauthId))
            .onItem().transform(v -> Response.noContent().build())
            .onFailure().invoke(e -> LOG.errorf("Failed to delete vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("vendorId");
                return Uni.createFrom().voidItem();
            });
    }
}