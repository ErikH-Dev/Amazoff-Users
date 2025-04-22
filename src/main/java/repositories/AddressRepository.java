package repositories;

import entities.Address;
import exceptions.errors.AddressNotFoundException;
import interfaces.IAddressRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class AddressRepository implements IAddressRepository {
    @Inject
    SessionFactory sessionFactory;

    @Override
    public Uni<Address> create(Address address) {
        return sessionFactory.withTransaction(session -> session.persist(address).replaceWith(address))
            .onFailure().invoke(e -> {
                throw new RuntimeException("Failed to create address: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<List<Address>> readAllByUser(int oauthId) {
        return sessionFactory.withSession(session -> 
            session.createQuery("SELECT a FROM Address a WHERE a.buyer.oauthId = :oauthId", Address.class)
                .setParameter("oauthId", oauthId)
                .getResultList()
        ).onFailure().invoke(e -> {
            throw new RuntimeException("Failed to retrieve addresses: " + e.getMessage(), e);
        });
    }

    @Override
    public Uni<Address> readById(int id) {
        return sessionFactory.withSession(session -> session.find(Address.class, id))
            .onItem().ifNull().failWith(() -> new AddressNotFoundException(id));
    }

    @Override
    public Uni<Address> update(Address address) {
        return sessionFactory.withTransaction(session -> 
            session.find(Address.class, address.getId())
                .onItem().ifNull().failWith(() -> new AddressNotFoundException(address.getId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(address))
        );
    }

    @Override
    public Uni<Void> delete(int id) {
        return sessionFactory.withTransaction(session -> session.find(Address.class, id)
            .onItem().ifNull().failWith(() -> new AddressNotFoundException(id))
            .onItem().ifNotNull().call(address -> {
                if (address.getBuyer() != null) {
                    address.getBuyer().getAddresses().remove(address);
                    address.setBuyer(null);
                }
                return session.remove(address);
            })
            .replaceWithVoid());
    }
}