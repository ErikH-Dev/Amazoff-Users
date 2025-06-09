package repositories;

import entities.Vendor;
import exceptions.errors.VendorNotFoundException;
import interfaces.IVendorRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

@ApplicationScoped
public class VendorRepository implements IVendorRepository {

    private static final Logger LOG = Logger.getLogger(VendorRepository.class);

    SessionFactory sessionFactory;
    public VendorRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Uni<Vendor> create(Vendor vendor) {
        LOG.debugf("Persisting new vendor: keycloakId=%s", vendor.getKeycloakId());
        return sessionFactory.withTransaction(session -> session.persist(vendor).replaceWith(vendor))
            .onItem().invoke(v -> LOG.debugf("Vendor persisted: keycloakId=%s", v.getKeycloakId()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to create vendor: %s", e.getMessage());
                throw new RuntimeException("Failed to create vendor: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<Vendor> read(String keycloakId) {
        LOG.debugf("Fetching vendor from DB: keycloakId=%s", keycloakId);
        return sessionFactory.withSession(session -> session.find(Vendor.class, keycloakId))
            .onItem().invoke(v -> {
                if (v != null) LOG.debugf("Vendor fetched: keycloakId=%s", v.getKeycloakId());
            })
            .onItem().ifNull().failWith(() -> new VendorNotFoundException(keycloakId));
    }

    @Override
    public Uni<Vendor> update(Vendor vendor) {
        LOG.debugf("Updating vendor: keycloakId=%s", vendor.getKeycloakId());
        return sessionFactory.withTransaction(session ->
            session.find(Vendor.class, vendor.getKeycloakId())
                .onItem().ifNull().failWith(() -> new VendorNotFoundException(vendor.getKeycloakId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(vendor))
        ).onItem().invoke(v -> LOG.debugf("Vendor updated: keycloakId=%s", v.getKeycloakId()));
    }

    @Override
    public Uni<Void> delete(String keycloakId) {
        LOG.debugf("Deleting vendor from DB: keycloakId=%s", keycloakId);
        return sessionFactory.withTransaction(session ->
            session.find(Vendor.class, keycloakId)
                .onItem().ifNull().failWith(() -> new VendorNotFoundException(keycloakId))
                .onItem().ifNotNull().call(vendor -> {
                    vendor.getProductIds().clear();
                    return session.flush();
                })
                .call(session::remove)
                .replaceWithVoid()
        ).invoke(() -> LOG.debugf("Vendor deleted from DB: keycloakId=%s", keycloakId))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to delete vendor: %s", e.getMessage());
                throw new RuntimeException("Failed to delete vendor: " + e.getMessage(), e);
            });
    }
}