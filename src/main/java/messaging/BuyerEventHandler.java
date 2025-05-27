package messaging;

import interfaces.IBuyerService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@ApplicationScoped
public class BuyerEventHandler {

    private static final Logger LOG = Logger.getLogger(BuyerEventHandler.class);
    IBuyerService buyerService;
    
    public BuyerEventHandler(IBuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @Incoming("get-buyer-requests")
    @Outgoing("get-buyer-responses")
    public Uni<Message<JsonObject>> handleGetBuyer(Message<JsonObject> msg) {
        int oauthId = msg.getPayload().getInteger("oauthId");
        MDC.put("buyerId", oauthId);
        LOG.infof("Handling get-buyer request: oauthId=%d", oauthId);
        return buyerService.read(oauthId)
            .onItem().transform(buyer -> {
                LOG.infof("Returning buyer for oauthId=%d", oauthId);
                JsonObject response = JsonObject.mapFrom(buyer);
                MDC.remove("buyerId");
                return Message.of(response);
            })
            .onFailure().recoverWithItem(e -> {
                LOG.errorf("Failed to handle get-buyer request: %s", e.getMessage());
                JsonObject errorResponse = new JsonObject()
                        .put("error", true)
                        .put("message", "Buyer not found")
                        .put("oauthId", oauthId);
                MDC.remove("buyerId");
                return Message.of(errorResponse);
            });
    }
}