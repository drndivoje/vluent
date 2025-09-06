package io.github.drndivoje.vluent.example;

import io.github.drndivoje.vluent.model.ValidateWith;
import java.time.LocalDate;

public record User(
    String name,
    @ValidateWith(value = OlderThen18Validator.class) LocalDate birthday,
    double salary) {}
