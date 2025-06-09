package interfaces;

import entities.Vendor;
import io.smallrye.mutiny.Uni;

public interface IVendorRepository {
    Uni<Vendor> create(Vendor vendor);
    Uni<Vendor> read(String keycloakId);
    Uni<Vendor> update(Vendor vendor);
    Uni<Void> delete(String keycloakId);
}