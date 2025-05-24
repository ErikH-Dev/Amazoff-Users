package controllers;

import dtos.CreateAddressRequest;
import dtos.UpdateAddressRequest;
import interfaces.IAddressService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Path("/addresses")
public class AddressController {
    private static final Logger LOG = Logger.getLogger(AddressController.class);
    private IAddressService addressService;

    public AddressController(IAddressService addressService) {
        this.addressService = addressService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addAddress(@Valid CreateAddressRequest addressRequest) {
        LOG.infof("Received addAddress request for oauthId=%d", addressRequest.oauthId);
        return addressService.create(addressRequest)
            .onItem().invoke(address -> {
                MDC.put("addressId", address.getId());
                LOG.infof("Address created: addressId=%d", address.getId());
                MDC.remove("addressId");
            })
            .onItem().transform(createdAddress -> Response.ok(createdAddress).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to create address: %s", e.getMessage()));
    }

    @GET
    @Path("/{oauthId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getAllAddressesByUser(@PathParam("oauthId") int oauthId) {
        LOG.infof("Received getAllAddressesByUser request: oauthId=%d", oauthId);
        return addressService.readAllByUser(oauthId)
            .onItem().invoke(addresses -> LOG.infof("Addresses retrieved for user: oauthId=%d, count=%d", oauthId, addresses.size()))
            .onItem().transform(addresses -> Response.ok(addresses).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to get addresses for user: %s", e.getMessage()));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateAddress(@Valid UpdateAddressRequest addressRequest) {
        MDC.put("addressId", addressRequest.id);
        LOG.infof("Received updateAddress request: addressId=%d", addressRequest.id);
        return addressService.update(addressRequest)
            .onItem().invoke(updatedAddress -> LOG.infof("Address updated: addressId=%d", updatedAddress.getId()))
            .onItem().transform(updatedAddress -> Response.ok(updatedAddress).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to update address: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("addressId");
                return Uni.createFrom().voidItem();
            });
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteAddress(@PathParam("id") int id) {
        MDC.put("addressId", id);
        LOG.infof("Received deleteAddress request: addressId=%d", id);
        return addressService.delete(id)
            .onItem().invoke(v -> LOG.infof("Address deleted: addressId=%d", id))
            .onItem().transform(v -> Response.noContent().build())
            .onFailure().invoke(e -> LOG.errorf("Failed to delete address: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("addressId");
                return Uni.createFrom().voidItem();
            });
    }
}