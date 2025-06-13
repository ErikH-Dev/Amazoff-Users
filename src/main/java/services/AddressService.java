package services;

import entities.Address;
import interfaces.IAddressRepository;
import interfaces.IAddressService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.util.List;

@ApplicationScoped
public class AddressService implements IAddressService {
    private static final Logger LOG = Logger.getLogger(AddressService.class);
    private final IAddressRepository addressRepository;

    public AddressService(IAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Uni<Address> create(Address address) {
        LOG.infof("Creating address for keycloakId=%d", address.getKeycloakId());
        return addressRepository.create(address)
            .invoke(created -> {
                MDC.put("addressId", created.getId());
                LOG.infof("Address created: addressId=%d", created.getId());
                MDC.remove("addressId");
            })
            .onFailure().invoke(e -> LOG.errorf("Failed to create address: %s", e.getMessage()));
    }

    @Override
    public Uni<Address> update(Address address) {
        LOG.infof("Updating address: addressId=%d", address.getId());
        return addressRepository.update(address)
            .invoke(updated -> LOG.infof("Address updated: addressId=%d", updated.getId()))
            .onFailure().invoke(e -> LOG.errorf("Failed to update address: %s", e.getMessage()));
    }

    @Override
    public Uni<Void> delete(int id) {
        LOG.infof("Deleting address: addressId=%d", id);
        return addressRepository.delete(id)
            .invoke(() -> LOG.infof("Address deleted: addressId=%d", id))
            .onFailure().invoke(e -> LOG.errorf("Failed to delete address: %s", e.getMessage()));
    }

    @Override
    public Uni<List<Address>> readAllByUser(String keycloakId) {
        LOG.infof("Fetching all addresses for user: keycloakId=%s", keycloakId);
        return addressRepository.readAllByUser(keycloakId)
            .invoke(addresses -> LOG.infof("Fetched %d addresses for user: keycloakId=%s", addresses.size(), keycloakId))
            .onFailure().invoke(e -> LOG.errorf("Failed to retrieve addresses: %s", e.getMessage()));
    }

    @Override
    public Uni<Address> findById(int id) {
        LOG.infof("Fetching address by id: addressId=%d", id);
        return addressRepository.findById(id)
            .invoke(address -> LOG.infof("Fetched address: addressId=%d", address.getId()))
            .onFailure().invoke(e -> LOG.errorf("Failed to retrieve address: %s", e.getMessage()));
    }
}