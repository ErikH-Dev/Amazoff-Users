package dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;

public class CreateAddressRequest {
    @NotBlank(message = "Street must not be blank")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    @JsonProperty("street")
    public String street;

    @NotBlank(message = "City must not be blank")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @JsonProperty("city")
    public String city;

    @NotBlank(message = "State must not be blank")
    @Size(max = 50, message = "State must not exceed 50 characters")
    @JsonProperty("state")
    public String state;

    @NotBlank(message = "Zip code must not be blank")
    @Size(max = 20, message = "Zip code must not exceed 20 characters")
    @JsonProperty("zipCode")
    public String zipCode;

    @NotBlank(message = "Country must not be blank")
    @Size(max = 50, message = "Country must not exceed 50 characters")
    @JsonProperty("country")
    public String country;
}
