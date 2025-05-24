package services;

import dtos.CreateBuyerRequest;
import dtos.UpdateBuyerRequest;
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
    public Uni<Buyer> create(CreateBuyerRequest request) {
        LOG.infof("Creating buyer: oauthId=%d, email=%s", request.oauthId, request.email);
        Buyer buyer = new Buyer(
            request.oauthId,
            request.oauthProvider,
            request.firstName,
            request.lastName,
            request.email
        );
        return buyerRepository.create(buyer)
            .invoke(created -> {
                MDC.put("buyerId", created.getOauthId());
                LOG.infof("Buyer persisted: oauthId=%d", created.getOauthId());
                MDC.remove("buyerId");
            })
            .onFailure().invoke(e -> LOG.errorf("Failed to create buyer: %s", e.getMessage()));
    }

    @Override
    public Uni<Buyer> read(int oauthId) {
        MDC.put("buyerId", oauthId);
        LOG.infof("Reading buyer: oauthId=%d", oauthId);
        return buyerRepository.read(oauthId)
            .invoke(buyer -> LOG.infof("Buyer read: oauthId=%d", buyer.getOauthId()))
            .onFailure().invoke(e -> LOG.errorf("Failed to read buyer: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("buyerId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<Buyer> update(UpdateBuyerRequest request) {
        MDC.put("buyerId", request.oauthId);
        LOG.infof("Updating buyer: oauthId=%d", request.oauthId);
        return buyerRepository.read(request.oauthId)
            .onItem().ifNotNull().transformToUni(existingBuyer -> {
                Buyer updatedBuyer = new Buyer(
                    request.oauthId,
                    request.oauthProvider,
                    request.firstName,
                    request.lastName,
                    request.email,
                    existingBuyer.getAddresses(),
                    existingBuyer.getOrderIds()
                );
                return buyerRepository.update(updatedBuyer)
                    .invoke(updated -> LOG.infof("Buyer updated: oauthId=%d", updated.getOauthId()));
            })
            .onFailure().invoke(e -> LOG.errorf("Failed to update buyer: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("buyerId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        MDC.put("buyerId", oauthId);
        LOG.infof("Deleting buyer: oauthId=%d", oauthId);
        return buyerRepository.delete(oauthId)
            .invoke(() -> LOG.infof("Buyer deleted: oauthId=%d", oauthId))
            .onFailure().invoke(e -> LOG.errorf("Failed to delete buyer: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("buyerId");
                return Uni.createFrom().voidItem();
            });
    }
}