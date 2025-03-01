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
}
