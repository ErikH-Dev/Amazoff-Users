package services;

import dtos.CreateVendorRequest;
import dtos.UpdateVendorRequest;
import entities.Vendor;
import interfaces.IVendorRepository;
import interfaces.IVendorService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;

import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@ApplicationScoped
public class VendorService implements IVendorService {
    private static final Logger LOG = Logger.getLogger(VendorService.class);
    private final IVendorRepository vendorRepository;

    public VendorService(IVendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public Uni<Vendor> create(Vendor vendor) {
        LOG.infof("Creating vendor: keycloakId=%d, storeName=%s", vendor.getKeycloakId(), vendor.getStoreName());
        return vendorRepository.create(vendor)
            .invoke(created -> {
                MDC.put("vendorId", created.getKeycloakId());
                LOG.infof("Vendor persisted: keycloakId=%d", created.getKeycloakId());
                MDC.remove("vendorId");
            })
            .onFailure().invoke(e -> LOG.errorf("Failed to create vendor: %s", e.getMessage()));
    }

    @Override
    public Uni<Vendor> read(String keycloakId) {
        MDC.put("vendorId", keycloakId);
        LOG.infof("Reading vendor: keycloakId=%s", keycloakId);
        return vendorRepository.read(keycloakId)
            .invoke(vendor -> LOG.infof("Vendor read: keycloakId=%s", vendor.getKeycloakId()))
            .onFailure().invoke(e -> LOG.errorf("Failed to read vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("vendorId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<Vendor> update(Vendor vendor) {
        MDC.put("vendorId", vendor.getKeycloakId());
        LOG.infof("Updating vendor: keycloakId=%s", vendor.getKeycloakId());
        return vendorRepository.update(vendor)
            .invoke(updated -> LOG.infof("Vendor updated: keycloakId=%s", updated.getKeycloakId()))
            .onFailure().invoke(e -> LOG.errorf("Failed to update vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("vendorId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<Void> delete(String keycloakId) {
        MDC.put("vendorId", keycloakId);
        LOG.infof("Deleting vendor: keycloakId=%s", keycloakId);
        return vendorRepository.delete(keycloakId)
            .invoke(() -> LOG.infof("Vendor deleted: keycloakId=%s", keycloakId))
            .onFailure().invoke(e -> LOG.errorf("Failed to delete vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("vendorId");
                return Uni.createFrom().voidItem();
            });
    }
}