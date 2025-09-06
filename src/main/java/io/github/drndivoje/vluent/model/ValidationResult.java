package io.github.drndivoje.vluent.model;

import java.util.LinkedList;
import java.util.List;

/**
 * The result of validation returned by {@link Validator}. It contains the invalidation messages if
 * the case of failed validation.
 */
public final class ValidationResult {

  private final List<String> invalidationMessages;

  ValidationResult() {
    this.invalidationMessages = new LinkedList<>();
  }

  ValidationResult(ValidationResult validationResult) {
    this.invalidationMessages = new LinkedList<>(validationResult.invalidationMessages);
  }

  /** The successful validation result */
  public static ValidationResult SUCCESS = new ValidationResult();

  private void add(String message) {
    invalidationMessages.add(message);
  }

  /**
   * It creates invalid validation result with concrete message
   *
   * @param message message to add to the invalidation messages
   * @return non-successful/failed {@link ValidationResult} instance with {@param message}
   */
  public static ValidationResult createError(String message) {
    ValidationResult validationResult = new ValidationResult();
    validationResult.add(message);
    return validationResult;
  }

  /**
   * It checks it the result is successful
   *
   * @return true if the validation is successful
   */
  public boolean isSuccess() {
    return invalidationMessages.isEmpty();
  }

  /**
   * It returns the invalidation messages
   *
   * @return list of invalidation messages
   */
  public List<String> getInvalidationMessages() {
    return invalidationMessages;
  }
}
