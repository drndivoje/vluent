package com.drnd.vluent;

import com.drnd.vluent.model.ValidationResult;
import com.drnd.vluent.model.Validator;

import java.util.function.Supplier;

/**
 * Single validation step in validation chain. It executes validation on related value using related validator
 *
 * @param <T> type of value to validate
 */
class Step<T> {
    private final Validator<T> validator;
    private final Supplier<T> value;

    Step(Validator<T> validator, Supplier<T> value) {
        this.validator = validator;
        this.value = value;
    }

    ValidationResult execute() {
        return validator.validate(value.get());
    }
}
