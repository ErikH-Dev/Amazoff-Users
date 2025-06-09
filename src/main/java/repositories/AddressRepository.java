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
    public Uni<List<Address>> readAllByUser(String keycloakId) {
        LOG.debugf("Fetching all addresses for user: keycloakId=%s", keycloakId);
        return sessionFactory.withSession(session ->
            session.createQuery("SELECT a FROM Address a WHERE a.buyer.keycloakId = :keycloakId", Address.class)
                .setParameter("keycloakId", keycloakId)
                .getResultList()
        ).onItem().invoke(list -> LOG.debugf("Fetched %d addresses for user: keycloakId=%s", list.size(), keycloakId))
        .onFailure().invoke(e -> {
            LOG.errorf("Failed to retrieve addresses: %s", e.getMessage());
            throw new RuntimeException("Failed to retrieve addresses: " + e.getMessage(), e);
        });
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
        LOG.debugf("Deleting address: addressId=%d", id);
        return sessionFactory.withTransaction(session -> 
            session.find(Address.class, id)
                .onItem().ifNull().failWith(() -> new AddressNotFoundException(id))
                .onItem().ifNotNull().transformToUni(found -> session.remove(found))
        ).onItem().invoke(() -> LOG.debugf("Address deleted: addressId=%d", id))
          .onFailure().invoke(e -> LOG.errorf("Failed to delete address: %s", e.getMessage()));
    }

    @Override
    public Uni<Address> findById(int id) {
        LOG.debugf("Fetching address by id: addressId=%d", id);
        return sessionFactory.withSession(session -> 
            session.find(Address.class, id)
                .onItem().ifNull().failWith(() -> new AddressNotFoundException(id))
        ).onItem().invoke(a -> LOG.debugf("Fetched address: addressId=%d", a.getId()))
          .onFailure().invoke(e -> LOG.errorf("Failed to retrieve address: %s", e.getMessage()));
    }
}