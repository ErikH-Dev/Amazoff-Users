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
    @NotNull(message = "OAuth ID must not be null")
    @JsonProperty("oauth_id")
    private int oauthId;

    @NotNull(message = "OAuth provider must not be null")
    @JsonProperty("oauth_provider")
    private int oauthProvider;

    protected User() {}

    protected User(int oauthId, int oauthProvider) {
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
    }

    public int getOauthId() {
        return oauthId;
    }

    public int getOauthProvider() {
        return oauthProvider;
    }
}