package entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "App_User")
public abstract class User {
    @Id
    @NotNull(message = "Keycloak ID must not be null")
    @JsonProperty("keycloak_id")
    private String keycloakId;

    protected User() {}

    protected User(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getKeycloakId() {
        return keycloakId;
    }
}