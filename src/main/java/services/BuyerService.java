package services;

import entities.Buyer;
import interfaces.IBuyerRepository;
import interfaces.IBuyerService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@ApplicationScoped
public class BuyerService implements IBuyerService {
    private static final Logger LOG = Logger.getLogger(BuyerService.class);
    private final IBuyerRepository buyerRepository;

    public BuyerService(IBuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    @Override
    public Uni<Buyer> create(Buyer buyer) {
        return buyerRepository.create(buyer)
            .invoke(created -> {
                MDC.put("buyerId", created.getKeycloakId());
                LOG.infof("Buyer persisted: keycloakId=%s", created.getKeycloakId());
                MDC.remove("buyerId");
            })
            .onFailure().invoke(e -> LOG.errorf("Failed to create buyer: %s", e.getMessage()));
    }

    @Override
    public Uni<Buyer> read(String keycloakId) {
        MDC.put("buyerId", keycloakId);
        LOG.infof("Reading buyer: keycloakId=%s", keycloakId);
        return buyerRepository.read(keycloakId)
            .invoke(buyer -> LOG.infof("Buyer read: keycloakId=%s", buyer.getKeycloakId()))
            .onFailure().invoke(e -> LOG.errorf("Failed to read buyer: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("buyerId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<Buyer> update(Buyer buyer) {
        MDC.put("buyerId", buyer.getKeycloakId());
        LOG.infof("Updating buyer: keycloakId=%s", buyer.getKeycloakId());
        return buyerRepository.update(buyer)
            .invoke(updated -> LOG.infof("Buyer updated: keycloakId=%s", updated.getKeycloakId()))
            .onFailure().invoke(e -> LOG.errorf("Failed to update buyer: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("buyerId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<Void> delete(String keycloakId) {
        MDC.put("buyerId", keycloakId);
        LOG.infof("Deleting buyer: keycloakId=%s", keycloakId);
        return buyerRepository.delete(keycloakId)
            .invoke(() -> LOG.infof("Buyer deleted: keycloakId=%s", keycloakId))
            .onFailure().invoke(e -> LOG.errorf("Failed to delete buyer: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("buyerId");
                return Uni.createFrom().voidItem();
            });
    }
}