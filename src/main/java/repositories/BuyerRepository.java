package repositories;

import entities.Buyer;
import exceptions.errors.BuyerNotFoundException;
import interfaces.IBuyerRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BuyerRepository implements IBuyerRepository {

    private static final Logger LOG = Logger.getLogger(BuyerRepository.class);

    SessionFactory sessionFactory;
    public BuyerRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Uni<Buyer> create(Buyer buyer) {
        LOG.debugf("Persisting new buyer: keycloakId=%s", buyer.getKeycloakId());
        return sessionFactory.withTransaction(session -> session.persist(buyer).replaceWith(buyer))
            .onItem().invoke(b -> LOG.debugf("Buyer persisted: keycloakId=%s", b.getKeycloakId()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to create buyer: %s", e.getMessage());
                throw new RuntimeException("Failed to create buyer: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<Buyer> read(String keycloakId) {
        LOG.debugf("Fetching buyer from DB: keycloakId=%s", keycloakId);
        return sessionFactory.withSession(session -> session.find(Buyer.class, keycloakId))
            .onItem().invoke(b -> {
                if (b != null) LOG.debugf("Buyer fetched: keycloakId=%s", b.getKeycloakId());
            })
            .onItem().ifNull().failWith(() -> new BuyerNotFoundException(keycloakId));
    }

    @Override
    public Uni<Buyer> update(Buyer buyer) {
        LOG.debugf("Updating buyer: keycloakId=%s", buyer.getKeycloakId());
        return sessionFactory.withTransaction(session ->
            session.find(Buyer.class, buyer.getKeycloakId())
                .onItem().ifNull().failWith(() -> new BuyerNotFoundException(buyer.getKeycloakId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(buyer))
        ).onItem().invoke(b -> LOG.debugf("Buyer updated: keycloakId=%s", b.getKeycloakId()));
    }

    @Override
    public Uni<Void> delete(String keycloakId) {
        LOG.debugf("Deleting buyer from DB: keycloakId=%s", keycloakId);
        return sessionFactory.withTransaction(session ->
            session.find(Buyer.class, keycloakId)
                .onItem().ifNull().failWith(() -> new BuyerNotFoundException(keycloakId))
                .onItem().ifNotNull().call(buyer -> {
                    buyer.getOrderIds().clear();
                    return session.flush();
                })
                .call(session::remove)
                .replaceWithVoid()
        ).invoke(() -> LOG.debugf("Buyer deleted from DB: keycloakId=%s", keycloakId))
          .onFailure().invoke(e -> {
              LOG.errorf("Failed to delete buyer: %s", e.getMessage());
              throw new RuntimeException("Failed to delete buyer: " + e.getMessage(), e);
          });
    }
}