package entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "App_Address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oauth_id", nullable = false)
    @JsonProperty("oauth_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "oauth_id")
    @JsonIdentityReference(alwaysAsId = true)
    private Buyer buyer;

    @NotBlank(message = "Street must not be blank")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    @JsonProperty("street")
    private String street;

    @NotBlank(message = "City must not be blank")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @JsonProperty("city")
    private String city;

    @NotBlank(message = "State must not be blank")
    @Size(max = 100, message = "State must not exceed 100 characters")
    @JsonProperty("state")
    private String state;

    @NotBlank(message = "Postal code must not be blank")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(name = "postal_code")
    @JsonProperty("postal_code")
    private String postalCode;

    @NotBlank(message = "Country must not be blank")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @JsonProperty("country")
    private String country;

    public Address() {
    }

    public Address(Buyer buyer, String street, String city, String state, String postalCode, String country) {
        this.buyer = buyer;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public Address(int id, Buyer buyer, String street, String city, String state, String postalCode, String country) {
        this.id = id;
        this.buyer = buyer;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }
}