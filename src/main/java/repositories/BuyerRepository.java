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
        LOG.debugf("Persisting new buyer: oauthId=%d", buyer.getOauthId());
        return sessionFactory.withTransaction(session -> session.persist(buyer).replaceWith(buyer))
            .onItem().invoke(b -> LOG.debugf("Buyer persisted: oauthId=%d", b.getOauthId()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to create buyer: %s", e.getMessage());
                throw new RuntimeException("Failed to create buyer: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<Buyer> read(int oauthId) {
        LOG.debugf("Fetching buyer from DB: oauthId=%d", oauthId);
        return sessionFactory.withSession(session -> session.find(Buyer.class, oauthId))
            .onItem().invoke(b -> {
                if (b != null) LOG.debugf("Buyer fetched: oauthId=%d", b.getOauthId());
            })
            .onItem().ifNull().failWith(() -> new BuyerNotFoundException(oauthId));
    }

    @Override
    public Uni<Buyer> update(Buyer buyer) {
        LOG.debugf("Updating buyer: oauthId=%d", buyer.getOauthId());
        return sessionFactory.withTransaction(session -> 
            session.find(Buyer.class, buyer.getOauthId())
                .onItem().ifNull().failWith(() -> new BuyerNotFoundException(buyer.getOauthId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(buyer))
        ).onItem().invoke(b -> LOG.debugf("Buyer updated: oauthId=%d", b.getOauthId()));
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        LOG.debugf("Deleting buyer from DB: oauthId=%d", oauthId);
        return sessionFactory.withTransaction(session -> 
            session.find(Buyer.class, oauthId)
                .onItem().ifNull().failWith(() -> new BuyerNotFoundException(oauthId))
                .onItem().ifNotNull().call(buyer -> {
                    buyer.getOrderIds().clear();
                    return session.flush();
                })
                .call(session::remove)
                .replaceWithVoid()
        ).invoke(() -> LOG.debugf("Buyer deleted from DB: oauthId=%d", oauthId));
    }
}