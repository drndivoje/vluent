package com.drndivoje.vluent.model;

/**
 * Interface to implement validation on single value
 *
 * @param <T>
 */
public interface Validator<T> {
    ValidationResult validate(T value);
}
