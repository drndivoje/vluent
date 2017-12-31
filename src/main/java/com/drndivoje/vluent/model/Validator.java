package com.drnd.vluent.model;

/**
 * Interface to implement validation on single value
 *
 * @param <T>
 */
public interface Validator<T> {
    ValidationResult validate(T value);
}
