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
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.util.List;
import dtos.CreateAddressRequest;
import dtos.UpdateAddressRequest;

@ApplicationScoped
public class AddressService implements IAddressService {
    private static final Logger LOG = Logger.getLogger(AddressService.class);
    private final IAddressRepository addressRepository;
    private final IBuyerRepository buyerRepository;

    public AddressService(IAddressRepository addressRepository, IBuyerRepository buyerRepository) {
        this.addressRepository = addressRepository;
        this.buyerRepository = buyerRepository;
    }

    @Override
    public Uni<Address> create(CreateAddressRequest addressRequest) {
        LOG.infof("Creating address for oauthId=%d", addressRequest.oauthId);
        return fetchBuyer(addressRequest.oauthId)
            .flatMap(buyer -> {
                Address address = new Address(buyer, addressRequest.street, addressRequest.city, addressRequest.state, addressRequest.postalCode, addressRequest.country);
                return addressRepository.create(address)
                    .onItem().transform(createdAddress -> {
                        MDC.put("addressId", createdAddress.getId());
                        LOG.infof("Address persisted: addressId=%d", createdAddress.getId());
                        MDC.remove("addressId");
                        createdAddress.setBuyer(buyer);
                        return createdAddress;
                    });
            })
            .onFailure().invoke(e -> LOG.errorf("Failed to create address: %s", e.getMessage()));
    }

    @Override
    public Uni<Address> update(UpdateAddressRequest addressRequest) {
        MDC.put("addressId", addressRequest.id);
        LOG.infof("Updating address: addressId=%d", addressRequest.id);
        return addressRepository.readById(addressRequest.id)
            .onItem().ifNull().failWith(new AddressNotFoundException(addressRequest.id))
            .flatMap(existingAddress -> fetchBuyer(addressRequest.oauthId)
                .flatMap(buyer -> {
                    Address newAddress = new Address(addressRequest.id, buyer, addressRequest.street, addressRequest.city, addressRequest.state, addressRequest.postalCode, addressRequest.country);
                    return addressRepository.update(newAddress)
                        .invoke(updated -> LOG.infof("Address updated: addressId=%d", updated.getId()));
                })
            )
            .onFailure().invoke(e -> LOG.errorf("Failed to update address: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("addressId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<Void> delete(int id) {
        MDC.put("addressId", id);
        LOG.infof("Deleting address: addressId=%d", id);
        return addressRepository.readById(id)
            .onItem().ifNull().failWith(new AddressNotFoundException(id))
            .flatMap(existingAddress -> addressRepository.delete(id)
                .invoke(() -> LOG.infof("Address deleted: addressId=%d", id)))
            .onFailure().invoke(e -> LOG.errorf("Failed to delete address: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("addressId");
                return Uni.createFrom().voidItem();
            });
    }

    @Override
    public Uni<List<Address>> readAllByUser(int oauthId) {
        LOG.infof("Reading all addresses for user: oauthId=%d", oauthId);
        return fetchBuyer(oauthId)
            .flatMap(buyer -> addressRepository.readAllByUser(oauthId)
                .invoke(addresses -> LOG.infof("Read %d addresses for user: oauthId=%d", addresses.size(), oauthId)))
            .onFailure().invoke(e -> LOG.errorf("Failed to read addresses for user: %s", e.getMessage()));
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