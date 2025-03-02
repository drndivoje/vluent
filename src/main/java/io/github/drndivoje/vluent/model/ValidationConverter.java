package io.github.drndivoje.vluent.model;

/**
 * It converts {@link ValidationResult} desirable type T defined by caller.
 *
 * @param <T> the target conversion type
 */
public interface ValidationConverter<T> {

    /**
     * It converts {@link ValidationResult} to desirable type T.
     * @param result the result of validation
     * @return the converted result
     */
    T convert(ValidationResult result);
}
