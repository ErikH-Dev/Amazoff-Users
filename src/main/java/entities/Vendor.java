package entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import dtos.ProductDTO;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "App_Vendor")
@DiscriminatorValue("VENDOR")
@PrimaryKeyJoinColumn(name = "keycloak_id")
public class Vendor extends User {
    @NotBlank(message = "Store name must not be blank")
    @Size(max = 100, message = "Store name must not exceed 100 characters")
    @JsonProperty("store_name")
    private String storeName;

    @ElementCollection(fetch = FetchType.EAGER)
    @JsonProperty("product_ids")
    private List<Integer> productIds = new ArrayList<>();

    @Transient
    @JsonProperty("products")
    private List<ProductDTO> products = new ArrayList<>();

    public Vendor() {}

    public Vendor(String keycloakId, String storeName) {
        super(keycloakId);
        this.storeName = storeName;
    }

    public Vendor(String keycloakId, String storeName, List<Integer> productIds) {
        super(keycloakId);
        this.storeName = storeName;
        this.productIds = productIds;
    }

    public String getStoreName() {
        return storeName;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public List<Integer> getProductIds() {
        return productIds;
    }
}