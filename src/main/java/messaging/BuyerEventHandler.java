package messaging;

import entities.Buyer;
import interfaces.IBuyerService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.*;

@ApplicationScoped
public class BuyerEventHandler {

    IBuyerService buyerService;
    public BuyerEventHandler(IBuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @Incoming("get-buyer-requests")
    @Outgoing("get-buyer-responses")
    public Uni<Message<JsonObject>> handleGetBuyer(Message<JsonObject> msg) {
        int oauthId = msg.getPayload().getInteger("oauthId");
        return buyerService.read(oauthId)
            .onItem().transform(buyer -> {
                JsonObject response = JsonObject.mapFrom(buyer);
                return Message.of(response);
            });
    }
}