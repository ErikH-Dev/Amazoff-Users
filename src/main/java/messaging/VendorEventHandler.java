package messaging;

import entities.Vendor;
import interfaces.IVendorService;
import io.smallrye.mutiny.Uni;
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
    public Uni<Message<Vendor>> handleGetVendor(Message<String> msg) {
        int oauthId = Integer.parseInt(msg.getPayload());
        return vendorService.read(oauthId)
            .onItem().transform(msg::withPayload);
    }
}