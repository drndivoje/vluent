package io.github.drndivoje.vluent.model;

import java.util.LinkedList;
import java.util.List;

/**
 * The result of validation returned by {@link Validator}. It contains the invalidation messages
 * if the case of non successful validation.
 */
public final class ValidationResult {
    private List<String> invalidationMessages = new LinkedList<>();
    public static ValidationResult SUCCESS = new ValidationResult();

    private void add(String message) {
        invalidationMessages.add(message);
    }

    /**
     * It creates invalid validation result with concrete message
     *
     * @return non successful {@link ValidationResult} instance with {@param message}
     */
    public static ValidationResult createError(String message) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(message);
        return validationResult;
    }

    public boolean isSuccess() {
        return invalidationMessages.isEmpty();
    }

    public List<String> getInvalidationMessages() {
        return invalidationMessages;
    }

}


