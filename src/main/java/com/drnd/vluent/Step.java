package com.drnd.vluent;

import com.drnd.vluent.model.ValidationResult;
import com.drnd.vluent.model.Validator;

import java.util.function.Supplier;

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
