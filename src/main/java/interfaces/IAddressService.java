package interfaces;

import entities.Address;
import io.smallrye.mutiny.Uni;

import java.util.List;

import dtos.CreateAddressRequest;
import dtos.UpdateAddressRequest;

public interface IAddressService {
    Uni<Address> create(CreateAddressRequest addressRequest);
    Uni<Address> update(UpdateAddressRequest addressRequest);
    Uni<Void> delete(int id);
    Uni<List<Address>> readAllByUser(int oauthId);
}