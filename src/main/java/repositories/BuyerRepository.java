package repositories;

import entities.Buyer;
import entities.User;
import exceptions.errors.BuyerNotFoundException;
import interfaces.IBuyerRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class BuyerRepository implements IBuyerRepository {
    @Inject
    SessionFactory sessionFactory;

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
                .call(buyer -> session.remove(buyer))
                .replaceWithVoid()
        );
    }
}