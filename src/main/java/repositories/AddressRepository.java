package repositories;

import entities.Address;
import exceptions.errors.AddressNotFoundException;
import interfaces.IAddressRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AddressRepository implements IAddressRepository {

    private static final Logger LOG = Logger.getLogger(AddressRepository.class);

    SessionFactory sessionFactory;
    public AddressRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Uni<Address> create(Address address) {
        LOG.debug("Persisting new address");
        return sessionFactory.withTransaction(session -> session.persist(address).replaceWith(address))
            .onItem().invoke(a -> LOG.debugf("Address persisted: addressId=%d", a.getId()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to create address: %s", e.getMessage());
                throw new RuntimeException("Failed to create address: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<List<Address>> readAllByUser(int oauthId) {
        LOG.debugf("Fetching all addresses for user: oauthId=%d", oauthId);
        return sessionFactory.withSession(session -> 
            session.createQuery("SELECT a FROM Address a WHERE a.buyer.oauthId = :oauthId", Address.class)
                .setParameter("oauthId", oauthId)
                .getResultList()
        ).onItem().invoke(list -> LOG.debugf("Fetched %d addresses for user: oauthId=%d", list.size(), oauthId))
        .onFailure().invoke(e -> {
            LOG.errorf("Failed to retrieve addresses: %s", e.getMessage());
            throw new RuntimeException("Failed to retrieve addresses: " + e.getMessage(), e);
        });
    }

    @Override
    public Uni<Address> readById(int id) {
        LOG.debugf("Fetching address from DB: addressId=%d", id);
        return sessionFactory.withSession(session -> session.find(Address.class, id))
            .onItem().invoke(a -> {
                if (a != null) LOG.debugf("Address fetched: addressId=%d", a.getId());
            })
            .onItem().ifNull().failWith(() -> new AddressNotFoundException(id));
    }

    @Override
    public Uni<Address> update(Address address) {
        LOG.debugf("Updating address: addressId=%d", address.getId());
        return sessionFactory.withTransaction(session -> 
            session.find(Address.class, address.getId())
                .onItem().ifNull().failWith(() -> new AddressNotFoundException(address.getId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(address))
        ).onItem().invoke(a -> LOG.debugf("Address updated: addressId=%d", a.getId()));
    }

    @Override
    public Uni<Void> delete(int id) {
        LOG.debugf("Deleting address from DB: addressId=%d", id);
        return sessionFactory.withTransaction(session -> session.find(Address.class, id)
            .onItem().ifNull().failWith(() -> new AddressNotFoundException(id))
            .onItem().ifNotNull().call(address -> {
                if (address.getBuyer() != null) {
                    address.getBuyer().getAddresses().remove(address);
                    address.setBuyer(null);
                }
                return session.remove(address);
            })
            .replaceWithVoid())
            .invoke(() -> LOG.debugf("Address deleted from DB: addressId=%d", id));
    }
}