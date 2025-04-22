package interfaces;

import dtos.CreateVendorRequest;
import dtos.UpdateVendorRequest;
import entities.Vendor;
import io.smallrye.mutiny.Uni;

public interface IVendorService {
    Uni<Vendor> create(CreateVendorRequest vendor);
    Uni<Vendor> read(int oauthId);
    Uni<Vendor> update(UpdateVendorRequest vendor);
    Uni<Void> delete(int oauthId);
}