package services;

import dtos.CreateVendorRequest;
import dtos.UpdateVendorRequest;
import entities.Vendor;
import interfaces.IVendorRepository;
import interfaces.IVendorService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
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
    public Uni<Vendor> create(CreateVendorRequest request) {
        LOG.infof("Creating vendor: oauthId=%d, storeName=%s", request.oauthId, request.storeName);
        Vendor vendor = new Vendor(request.oauthId, request.oauthProvider, request.storeName);
        return vendorRepository.create(vendor)
            .invoke(created -> {
                MDC.put("vendorId", created.getOauthId());
                LOG.infof("Vendor persisted: oauthId=%d", created.getOauthId());
                MDC.remove("vendorId");
            })
            .onFailure().invoke(e -> LOG.errorf("Failed to create vendor: %s", e.getMessage()));
    }

    @Override
    public Uni<Vendor> read(int oauthId) {
        MDC.put("vendorId", oauthId);
        LOG.infof("Reading vendor: oauthId=%d", oauthId);
        return vendorRepository.read(oauthId)
            .invoke(vendor -> LOG.infof("Vendor read: oauthId=%d", vendor.getOauthId()))
            .onFailure().invoke(e -> LOG.errorf("Failed to read vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("vendorId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<Vendor> update(UpdateVendorRequest request) {
        MDC.put("vendorId", request.oauthId);
        LOG.infof("Updating vendor: oauthId=%d", request.oauthId);
        return vendorRepository.read(request.oauthId)
            .onItem().ifNotNull().transformToUni(existingVendor -> {
                Vendor updatedVendor = new Vendor(request.oauthId, request.oauthProvider, request.storeName, existingVendor.getProductIds());
                return vendorRepository.update(updatedVendor)
                    .invoke(updated -> LOG.infof("Vendor updated: oauthId=%d", updated.getOauthId()));
            })
            .onFailure().invoke(e -> LOG.errorf("Failed to update vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("vendorId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        MDC.put("vendorId", oauthId);
        LOG.infof("Deleting vendor: oauthId=%d", oauthId);
        return vendorRepository.delete(oauthId)
            .invoke(() -> LOG.infof("Vendor deleted: oauthId=%d", oauthId))
            .onFailure().invoke(e -> LOG.errorf("Failed to delete vendor: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("vendorId");
                return Uni.createFrom().voidItem();
            });
    }
}