package rocks.drnd.vluent_example;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.drndivoje.vluent.api.ValidateBeanWith;
import jakarta.validation.constraints.NotBlank;

@ValidateBeanWith(AddressValidator.class)
public record Address(
    @JsonProperty("street") @NotBlank String street,
    @JsonProperty("city") @NotBlank String city,
    @JsonProperty("zip_code") String zipCode,
    @JsonProperty("country") @NotBlank String country) {}
