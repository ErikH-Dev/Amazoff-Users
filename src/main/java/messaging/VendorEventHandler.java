package messaging;

import interfaces.IVendorService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@ApplicationScoped
public class VendorEventHandler {

    private static final Logger LOG = Logger.getLogger(VendorEventHandler.class);
    IVendorService vendorService;

    public VendorEventHandler(IVendorService vendorService) {
        this.vendorService = vendorService;
    }

    @Incoming("get-vendor-requests")
    @Outgoing("get-vendor-responses")
    public Uni<Message<JsonObject>> handleGetVendor(Message<JsonObject> msg) {
        String keycloakId = msg.getPayload().getString("keycloakId");
        MDC.put("vendorId", keycloakId);
        LOG.infof("Handling get-vendor request: keycloakId=%s", keycloakId);
        return vendorService.read(keycloakId)
                .onItem().transform(vendor -> {
                    LOG.infof("Returning vendor for keycloakId=%s", keycloakId);
                    JsonObject response = JsonObject.mapFrom(vendor);
                    MDC.remove("vendorId");
                    return Message.of(response);
                })
                .onFailure().recoverWithItem(e -> {
                    LOG.errorf("Failed to handle get-vendor request: %s", e.getMessage());
                    JsonObject errorResponse = new JsonObject()
                            .put("error", true)
                            .put("message", "Vendor not found")
                            .put("keycloakId", keycloakId);
                    MDC.remove("vendorId");
                    return Message.of(errorResponse);
                });
    }
}