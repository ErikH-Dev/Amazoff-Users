package messaging;

import entities.Vendor;
import interfaces.IVendorService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.*;

@ApplicationScoped
public class VendorEventHandler {

    IVendorService vendorService;
    public VendorEventHandler(IVendorService vendorService) {
        this.vendorService = vendorService;
    }

    @Incoming("get-vendor-requests")
    @Outgoing("get-vendor-responses")
    public Uni<Message<JsonObject>> handleGetVendor(Message<JsonObject> msg) {
        int oauthId = msg.getPayload().getInteger("oauthId");
        return vendorService.read(oauthId)
            .onItem().transform(vendor -> {
                JsonObject response = JsonObject.mapFrom(vendor);
                return Message.of(response);
            });
    }
}