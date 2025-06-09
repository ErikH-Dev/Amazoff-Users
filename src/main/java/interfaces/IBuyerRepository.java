package interfaces;

import entities.Buyer;
import io.smallrye.mutiny.Uni;

public interface IBuyerRepository {
    Uni<Buyer> create(Buyer buyer);
    Uni<Buyer> read(String keycloakId);
    Uni<Buyer> update(Buyer buyer);
    Uni<Void> delete(String keycloakId);
}