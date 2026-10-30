package rocks.drnd.vluent_example;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record User(
    @JsonProperty("name") @NotBlank String name,
    @JsonProperty("birth_date") LocalDate birthDate,
    @JsonProperty("salary") double salary) {}
