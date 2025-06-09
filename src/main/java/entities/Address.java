package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Keycloak ID must not be null")
    @Column(name = "keycloak_id", nullable = false)
    @JsonProperty("keycloak_id")
    private String keycloakId;

    @NotNull(message = "Street must not be null")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String street;

    @NotNull(message = "City must not be null")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String city;

    @NotNull(message = "State must not be null")
    @Size(max = 50, message = "State must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String state;

    @NotNull(message = "Zip code must not be null")
    @Size(max = 20, message = "Zip code must not exceed 20 characters")
    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @NotNull(message = "Country must not be null")
    @Size(max = 50, message = "Country must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keycloak_id", referencedColumnName = "keycloakId", insertable = false, updatable = false)
    private Buyer buyer;

    // Default constructor for JPA
    public Address() {
    }

    // Constructor
    public Address(String keycloakId, String street, String city, String state, String zipCode, String country) {
        this.keycloakId = keycloakId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", keycloakId='" + keycloakId + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}