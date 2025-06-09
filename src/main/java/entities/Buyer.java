package entities;

import java.util.ArrayList;
import java.util.List;

import dtos.OrderDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "App_Buyer")
@DiscriminatorValue("BUYER")
@PrimaryKeyJoinColumn(name = "oauth_id")
public class Buyer extends User {
    @NotBlank(message = "First name must not be blank")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @JsonProperty("email")
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @JsonProperty("order_ids")
    private List<Integer> orderIds = new ArrayList<>();

    @Transient
    @JsonProperty("orders")
    private List<OrderDTO> orders = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Address> addresses = new ArrayList<>();

    public Buyer() {}

    public Buyer(String keycloakId, String firstName, String lastName, String email) {
        super(keycloakId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Buyer(String keycloakId, String firstName, String lastName, String email, List<Address> addresses, List<Integer> orderIds) {
        super(keycloakId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addresses = addresses;
        this.orderIds = orderIds;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public List<Integer> getOrderIds() {
        return orderIds;
    }

    public List<Address> getAddresses() {
        return addresses;
    }


}