package services;

import dtos.CreateVendorRequest;
import dtos.UpdateVendorRequest;
import entities.Vendor;
import interfaces.IVendorRepository;
import interfaces.IVendorService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VendorService implements IVendorService {
    private final IVendorRepository vendorRepository;

    public VendorService(IVendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public Uni<Vendor> create(CreateVendorRequest request) {
        Vendor vendor = new Vendor(request.oauthId, request.oauthProvider, request.storeName);
        return vendorRepository.create(vendor);
    }

    @Override
    public Uni<Vendor> read(int oauthId) {
        return vendorRepository.read(oauthId);
    }

    @Override
    public Uni<Vendor> update(UpdateVendorRequest request) {
        return vendorRepository.read(request.oauthId)
            .onItem().ifNotNull().transformToUni(existingVendor -> {
                Vendor updatedVendor = new Vendor(request.oauthId, request.oauthProvider, request.storeName, existingVendor.getProductIds());                
                return vendorRepository.update(updatedVendor);
            });
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        return vendorRepository.delete(oauthId);
    }
}