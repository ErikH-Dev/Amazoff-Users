package dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;

public class UpdateAddressRequest {
    @NotNull(message = "Id must not be null")
    @JsonProperty("id")
    public int id;
    
    @NotNull(message = "oauthId must not be null")
    @JsonProperty("oauthId")
    public int oauthId;

    @NotBlank(message = "Street must not be blank")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    @JsonProperty("street")
    public String street;

    @NotBlank(message = "City must not be blank")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @JsonProperty("city")
    public String city;

    @NotBlank(message = "State must not be blank")
    @Size(max = 100, message = "State must not exceed 100 characters")
    @JsonProperty("state")
    public String state;

    @NotBlank(message = "Postal code must not be blank")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @JsonProperty("postal_code")
    public String postalCode;

    @NotBlank(message = "Country must not be blank")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @JsonProperty("country")
    public String country;
}
