package rocks.drnd.vluent_example;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.drndivoje.vluent.model.ValidateBeanWith;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@ValidateBeanWith(UserValidator.class)
public record User(
    @JsonProperty("name") @NotBlank String name,
    @JsonProperty("birth_date") LocalDate birthDate,
    @JsonProperty("salary") double salary) {}
