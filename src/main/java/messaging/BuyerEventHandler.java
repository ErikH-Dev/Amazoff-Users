package messaging;

import entities.Buyer;
import interfaces.IBuyerService;
import io.smallrye.mutiny.Uni;
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
    public Uni<Message<Buyer>> handleGetBuyer(Message<String> msg) {
        int oauthId = Integer.parseInt(msg.getPayload());
        return buyerService.read(oauthId)
            .onItem().transform(msg::withPayload);
    }
}