package dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;

public class UpdateBuyerRequest {
    @NotNull(message = "First name must not be blank")
    @JsonProperty("oauthId")
    public int oauthId;

    @NotNull(message = "OAuth provider must not be blank")
    @JsonProperty("oauthProvider")
    public int oauthProvider;

    @NotBlank(message = "First name must not be blank")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @JsonProperty("firstName")
    public String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @JsonProperty("lastName")
    public String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @JsonProperty("email")
    public String email;
}
