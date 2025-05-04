package repositories;

import entities.Buyer;
import exceptions.errors.BuyerNotFoundException;
import interfaces.IBuyerRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class BuyerRepository implements IBuyerRepository {

    SessionFactory sessionFactory;
    public BuyerRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Uni<Buyer> create(Buyer buyer) {
        return sessionFactory.withTransaction(session -> session.persist(buyer).replaceWith(buyer))
            .onFailure().invoke(e -> {
                throw new RuntimeException("Failed to create buyer: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<Buyer> read(int oauthId) {
        return sessionFactory.withSession(session -> session.find(Buyer.class, oauthId))
            .onItem().ifNull().failWith(() -> new BuyerNotFoundException(oauthId));
    }

    @Override
    public Uni<Buyer> update(Buyer buyer) {
        return sessionFactory.withTransaction(session -> 
            session.find(Buyer.class, buyer.getOauthId())
                .onItem().ifNull().failWith(() -> new BuyerNotFoundException(buyer.getOauthId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(buyer))
        );
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        return sessionFactory.withTransaction(session -> 
            session.find(Buyer.class, oauthId)
                .onItem().ifNull().failWith(() -> new BuyerNotFoundException(oauthId))
                .onItem().ifNotNull().call(buyer -> {
                    buyer.getOrderIds().clear();
                    return session.flush();
                })
                .call(session::remove)
                .replaceWithVoid()
        );
    }
}