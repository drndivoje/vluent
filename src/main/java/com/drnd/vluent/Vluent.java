package com.drnd.vluent;

import com.drnd.vluent.model.Precondition;
import com.drnd.vluent.model.ValidationConverter;
import com.drnd.vluent.model.ValidationResult;
import com.drnd.vluent.model.Validator;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * The Entry point for creating validation chain. Adding validation steps does not invoke validation.
 * The validation is invoked calling method validate and validateAndConvert
 */
public class Vluent {

    private final Chain chain;

    private Vluent() {
        this.chain = new Chain();
    }

    public static Vluent create() {
        return new Vluent();
    }

    public <T> Vluent on(T toValidate, Validator<T> validator) {
        chain.add(toValidate, validator);
        return this;
    }

    public <T> Vluent on(Supplier<T> valueSupplier, Validator<T> validator) {
        chain.add(valueSupplier, validator);
        return this;
    }


    public ValidatorWithPrecondition when(Precondition condition) {
        return new ValidatorWithPrecondition(condition, this);
    }

    public <T> Vluent forEach(Collection<T> values, Validator<T> validator) {
        for (T value : values) {
            chain.add(value, validator);
        }
        return this;
    }

    public ValidationResult validate() {

        for (Step step : chain) {
            ValidationResult result = step.execute();
            if (!result.isSuccess()) {
                return result;
            }
        }
        return ValidationResult.SUCCESS;
    }

    public <T> T validateAndConvert(ValidationConverter<T> validationResultConverter) {
        return validationResultConverter.convert(this.validate());
    }

    public static class ValidatorWithPrecondition {
        private Precondition precondition;
        private final Vluent sourceValidator;

        private ValidatorWithPrecondition(Precondition precondition, Vluent sourceValidator) {
            this.precondition = precondition;
            this.sourceValidator = sourceValidator;
        }

        public <T> Vluent then(T toValidate, Validator<T> validator) {
            if (precondition.get()) {
                sourceValidator.on(toValidate, validator);
            }
            return sourceValidator;
        }

        public <T> ValidatorWithPrecondition thenForEach(Collection<T> values, Validator<T> validator) {
            if (precondition.get()) {
                sourceValidator.forEach(values, validator);
            }
            return this;
        }
    }
}
