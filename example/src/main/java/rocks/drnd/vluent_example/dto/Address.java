package rocks.drnd.vluent_example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.drndivoje.vluent.api.ValidateBeanWith;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rocks.drnd.vluent_example.validation.AddressValidator;

@ValidateBeanWith(AddressValidator.class)
public record Address(
    @JsonProperty("street") @NotBlank String street,
    @JsonProperty("city") @NotBlank String city,
    @JsonProperty("zip_code") String zipCode,
    @JsonProperty("country") @NotBlank String country) {}
