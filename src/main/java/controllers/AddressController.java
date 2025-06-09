package controllers;

import dtos.CreateAddressRequest;
import dtos.UpdateAddressRequest;
import interfaces.IAddressService;
import utils.JwtUtil;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;
import entities.Address;

@Path("/addresses")
@RolesAllowed({ "buyer", "admin" })
public class AddressController {
    private static final Logger LOG = Logger.getLogger(AddressController.class);
    private IAddressService addressService;
    private JwtUtil jwtUtil;

    public AddressController(IAddressService addressService, JwtUtil jwtUtil) {
        this.addressService = addressService;
        this.jwtUtil = jwtUtil;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addAddress(@Valid CreateAddressRequest addressRequest) {
        Address address = new Address(
                jwtUtil.getCurrentKeycloakUserId(),
                addressRequest.street,
                addressRequest.city,
                addressRequest.state,
                addressRequest.zipCode,
                addressRequest.country);

        LOG.infof("Received addAddress request for authenticated user: keycloakId=%s",
                jwtUtil.getCurrentKeycloakUserId());
        return addressService.create(address)
                .onItem().invoke(createdAddress -> {
                    MDC.put("addressId", createdAddress.getId());
                    LOG.infof("Address created: addressId=%d", createdAddress.getId());
                    MDC.remove("addressId");
                })
                .onItem()
                .transform(createdAddress -> Response.status(Response.Status.CREATED).entity(createdAddress).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to create address: %s", e.getMessage()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getCurrentUserAddresses() {
        String keycloakId = jwtUtil.getCurrentKeycloakUserId();
        LOG.infof("Received getAllAddressesByUser request for authenticated user: keycloakId=%s", keycloakId);

        return addressService.readAllByUser(keycloakId)
                .onItem()
                .invoke(addresses -> LOG.infof("Addresses retrieved for user: keycloakId=%s, count=%d", keycloakId,
                        addresses.size()))
                .onItem().transform(addresses -> Response.ok(addresses).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to get addresses for user: %s", e.getMessage()));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateAddress(@Valid UpdateAddressRequest addressRequest) {
        Address address = new Address(
                jwtUtil.getCurrentKeycloakUserId(),
                addressRequest.street,
                addressRequest.city,
                addressRequest.state,
                addressRequest.zipCode,
                addressRequest.country);
        MDC.put("addressId", addressRequest.id);
        LOG.infof("Received updateAddress request for authenticated user: keycloakId=%s, addressId=%d",
                jwtUtil.getCurrentKeycloakUserId(), addressRequest.id);

        return addressService.update(address)
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
        String currentUserId = jwtUtil.getCurrentKeycloakUserId();

        return addressService.findById(id)
                .onItem().transformToUni(address -> {
                    if (!address.getKeycloakId().equals(currentUserId)) {
                        return Uni.createFrom().item(Response.status(Response.Status.FORBIDDEN).build());
                    }
                    return addressService.delete(id)
                            .onItem().transform(unused -> Response.noContent().build());
                });
    }
}