package dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateVendorRequest {
    @NotNull(message = "oauthId must not be null")
    @JsonProperty("oauthId")
    public int oauthId;

    @NotNull(message = "oauthProvider must not be null")
    @JsonProperty("oauthProvider")
    public int oauthProvider;

    @NotBlank(message = "Store name must not be blank")
    @Size(max = 100, message = "Store name must not exceed 100 characters")
    @JsonProperty("storeName")
    public String storeName;
}