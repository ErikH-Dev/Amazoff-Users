package exceptions.errors;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class AddressNotFoundException extends WebApplicationException {
    public AddressNotFoundException(int id) {
        super("Address with id " + id + " not found", Response.Status.NOT_FOUND);
    }
}