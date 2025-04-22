package services;

import dtos.CreateBuyerRequest;
import dtos.UpdateBuyerRequest;
import entities.Buyer;
import interfaces.IBuyerRepository;
import interfaces.IBuyerService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BuyerService implements IBuyerService {
    private final IBuyerRepository buyerRepository;

    public BuyerService(IBuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    @Override
    public Uni<Buyer> create(CreateBuyerRequest request) {
        Buyer buyer = new Buyer(
            request.oauthId, 
            request.oauthProvider, 
            request.firstName, 
            request.lastName, 
            request.email
        );
        return buyerRepository.create(buyer);
    }

    @Override
    public Uni<Buyer> read(int oauthId) {
        return buyerRepository.read(oauthId);
    }

    @Override
    public Uni<Buyer> update(UpdateBuyerRequest request) {
        return buyerRepository.read(request.oauthId)
            .onItem().ifNotNull().transformToUni(existingBuyer -> {
                Buyer updatedBuyer = new Buyer(
                    request.oauthId,
                    request.oauthProvider,
                    request.firstName,
                    request.lastName,
                    request.email,
                    existingBuyer.getAddresses(),
                    existingBuyer.getOrderIds()
                );             
                return buyerRepository.update(updatedBuyer);
            });
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        return buyerRepository.delete(oauthId);
    }
}