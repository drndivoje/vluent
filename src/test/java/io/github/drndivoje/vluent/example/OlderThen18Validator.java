package io.github.drndivoje.vluent.example;

import io.github.drndivoje.vluent.model.ValidationResult;
import io.github.drndivoje.vluent.model.Validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class OlderThen18Validator implements Validator<LocalDate> {
    @Override
    public ValidationResult validate(LocalDate value) {
        LocalDate now = LocalDate.now();
        long between = ChronoUnit.YEARS.between(value, now);
        if (between >= 18) return ValidationResult.SUCCESS;
        else return ValidationResult.createError("to young");
    }
}
