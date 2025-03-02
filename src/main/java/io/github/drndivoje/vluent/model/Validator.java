package io.github.drndivoje.vluent.model;

/**
 * Interface to implement validation on single value
 *
 * @param <T> the type of value to be validated
 */
public interface Validator<T> {
    /**
     * It validates the value
     *
     * @param value the value to be validated
     * @return the result of validation
     */
    ValidationResult validate(T value);

    /**
     * It inverts the validation result. If the validation is successful it returns failed validation
     * result with no messages. If the validation is failed it returns successful validation result.
     *
     * @return the inverted validation result
     */
    default Validator<T> invert() {
        return value -> {
            ValidationResult result = validate(value);
            if (result.isSuccess()) return new ValidationResult(result);
            else return ValidationResult.SUCCESS;
        };
    }
}
