package interfaces;

import entities.Address;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface IAddressRepository {
    Uni<Address> create(Address address);
    Uni<Address> update(Address address);
    Uni<Void> delete(int id);
    Uni<List<Address>> readAllByUser(int userId);
    Uni<Address> readById(int id);
}