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
        LOG.debugf("Persisting new vendor: oauthId=%d", vendor.getOauthId());
        return sessionFactory.withTransaction(session -> session.persist(vendor).replaceWith(vendor))
            .onItem().invoke(v -> LOG.debugf("Vendor persisted: oauthId=%d", v.getOauthId()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to create vendor: %s", e.getMessage());
                throw new RuntimeException("Failed to create vendor: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<Vendor> read(int oauthId) {
        LOG.debugf("Fetching vendor from DB: oauthId=%d", oauthId);
        return sessionFactory.withSession(session -> session.find(Vendor.class, oauthId))
            .onItem().invoke(v -> {
                if (v != null) LOG.debugf("Vendor fetched: oauthId=%d", v.getOauthId());
            })
            .onItem().ifNull().failWith(() -> new VendorNotFoundException(oauthId));
    }

    @Override
    public Uni<Vendor> update(Vendor vendor) {
        LOG.debugf("Updating vendor: oauthId=%d", vendor.getOauthId());
        return sessionFactory.withTransaction(session -> 
            session.find(Vendor.class, vendor.getOauthId())
                .onItem().ifNull().failWith(() -> new VendorNotFoundException(vendor.getOauthId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(vendor))
        ).onItem().invoke(v -> LOG.debugf("Vendor updated: oauthId=%d", v.getOauthId()));
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        LOG.debugf("Deleting vendor from DB: oauthId=%d", oauthId);
        return sessionFactory.withTransaction(session -> 
            session.find(Vendor.class, oauthId)
                .onItem().ifNull().failWith(() -> new VendorNotFoundException(oauthId))
                .onItem().ifNotNull().call(vendor -> {
                    vendor.getProductIds().clear();
                    return session.flush();
                })
                .call(session::remove)
                .replaceWithVoid()
        ).invoke(() -> LOG.debugf("Vendor deleted from DB: oauthId=%d", oauthId));
    }
}