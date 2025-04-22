package services;

import entities.Address;
import entities.Buyer;
import exceptions.errors.AddressNotFoundException;
import exceptions.errors.BuyerNotFoundException;
import interfaces.IAddressRepository;
import interfaces.IBuyerRepository;
import interfaces.IAddressService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import dtos.CreateAddressRequest;
import dtos.UpdateAddressRequest;

@ApplicationScoped
public class AddressService implements IAddressService {
    private final IAddressRepository addressRepository;
    private final IBuyerRepository buyerRepository;

    public AddressService(IAddressRepository addressRepository, IBuyerRepository buyerRepository) {
        this.addressRepository = addressRepository;
        this.buyerRepository = buyerRepository;
    }

    @Override
    public Uni<Address> create(CreateAddressRequest addressRequest) {
        return fetchBuyer(addressRequest.oauthId)
            .flatMap(buyer -> {
                Address address = new Address(buyer, addressRequest.street, addressRequest.city, addressRequest.state, addressRequest.postalCode, addressRequest.country);
                return addressRepository.create(address)
                    .onItem().transform(createdAddress -> {
                        createdAddress.setBuyer(buyer);
                        return createdAddress;
                    });
            });
    }

    @Override
    public Uni<Address> update(UpdateAddressRequest addressRequest) {
        return addressRepository.readById(addressRequest.id)
            .onItem().ifNull().failWith(new AddressNotFoundException(addressRequest.id))
            .flatMap(existingAddress -> fetchBuyer(addressRequest.oauthId)
                .flatMap(buyer -> {
                    Address newAddress = new Address(addressRequest.id, buyer, addressRequest.street, addressRequest.city, addressRequest.state, addressRequest.postalCode, addressRequest.country);
                    return addressRepository.update(newAddress);
                })
            );
    }

    @Override
    public Uni<Void> delete(int id) {
        return addressRepository.readById(id)
            .onItem().ifNull().failWith(new AddressNotFoundException(id))
            .flatMap(existingAddress -> addressRepository.delete(id));
    }

    @Override
    public Uni<List<Address>> readAllByUser(int oauthId) {
        return fetchBuyer(oauthId)
            .flatMap(buyer -> addressRepository.readAllByUser(oauthId));
    }

    /**
     * Helper method to fetch a Buyer by oauthId.
     * @param oauthId The OAuth ID of the buyer.
     * @return A Uni containing the Buyer or an error if not found.
     */
    private Uni<Buyer> fetchBuyer(int oauthId) {
        return buyerRepository.read(oauthId)
            .onItem().ifNull().failWith(new BuyerNotFoundException(oauthId));
    }
}