package rocks.drnd.vluent_example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record User(
    @JsonProperty("id") String id,
    @JsonProperty("firstName") @NotBlank String firstName,
    @JsonProperty("lastName") @NotBlank String lastName,
    @JsonProperty("birthDate") LocalDate birthDate) {}
