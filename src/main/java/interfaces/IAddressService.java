package interfaces;

import entities.Address;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;

import java.util.List;

public interface IAddressService {
    Uni<Address> create(@Valid Address address);
    Uni<Address> update(@Valid Address address);
    Uni<Void> delete(int id);
    Uni<List<Address>> readAllByUser(String keycloakId);
    Uni<Address> findById(int id);
}