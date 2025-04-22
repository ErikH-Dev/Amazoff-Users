package repositories;

import entities.Vendor;
import exceptions.errors.VendorNotFoundException;
import interfaces.IVendorRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class VendorRepository implements IVendorRepository {
    @Inject
    SessionFactory sessionFactory;

    @Override
    public Uni<Vendor> create(Vendor vendor) {
        return sessionFactory.withTransaction(session -> session.persist(vendor).replaceWith(vendor))
            .onFailure().invoke(e -> {
                throw new RuntimeException("Failed to create vendor: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<Vendor> read(int oauthId) {
        return sessionFactory.withSession(session -> session.find(Vendor.class, oauthId))
            .onItem().ifNull().failWith(() -> new VendorNotFoundException(oauthId));
    }

    @Override
    public Uni<Vendor> update(Vendor vendor) {
        return sessionFactory.withTransaction(session -> 
            session.find(Vendor.class, vendor.getOauthId())
                .onItem().ifNull().failWith(() -> new VendorNotFoundException(vendor.getOauthId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(vendor))
        );
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        return sessionFactory.withTransaction(session -> 
            session.find(Vendor.class, oauthId)
                .onItem().ifNull().failWith(() -> new VendorNotFoundException(oauthId))
                .onItem().ifNotNull().call(vendor -> {
                    vendor.getProductIds().clear();
                    return session.flush();
                })
                .call(vendor -> session.remove(vendor))
                .replaceWithVoid()
        );
    }
}