package exceptions.errors;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class VendorNotFoundException extends WebApplicationException {
    public VendorNotFoundException(String keycloakId) {
        super("Vendor with id " + keycloakId + " not found", Response.Status.NOT_FOUND);
    }
}