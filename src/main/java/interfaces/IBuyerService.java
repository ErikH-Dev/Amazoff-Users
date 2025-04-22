package interfaces;

import dtos.CreateBuyerRequest;
import dtos.UpdateBuyerRequest;
import entities.Buyer;
import io.smallrye.mutiny.Uni;

public interface IBuyerService {
    Uni<Buyer> create(CreateBuyerRequest buyer);
    Uni<Buyer> read(int oauthId);
    Uni<Buyer> update(UpdateBuyerRequest buyer);
    Uni<Void> delete(int oauthId);
}