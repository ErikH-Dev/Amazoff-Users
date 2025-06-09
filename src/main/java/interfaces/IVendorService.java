package interfaces;

import entities.Vendor;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;

public interface IVendorService {
    Uni<Vendor> create(@Valid Vendor vendor);
    Uni<Vendor> read(String keycloakId);
    Uni<Vendor> update(@Valid Vendor vendor);
    Uni<Void> delete(String keycloakId);
}