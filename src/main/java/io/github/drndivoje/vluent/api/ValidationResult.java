package io.github.drndivoje.vluent.api;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of validation returned by {@link Validator}. It contains the invalidation messages if
 * the case of failed validation.
 */
public final class ValidationResult {

  private String errorMessage;

  private final List<String> errors;

  private ValidationResult() {
    this.errors = new ArrayList<>();
  }

  private ValidationResult(List<String> errors) {
    this.errors = errors;
  }

  /** The successful validation result */
  public static final ValidationResult SUCCESS = new ValidationResult();

  private void add(String message) {
    this.errors.add(message);
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
   * It creates invalid validation result with a list of error messages
   *
   * @param errors the list of error messages to add to the invalidation messages
   * @return non-successful/failed {@link ValidationResult} instance with the provided error
   *     messages
   */
  public static ValidationResult createError(List<String> errors) {
    return new ValidationResult(errors);
  }

  /**
   * It checks it the result is successful
   *
   * @return true if the validation is successful
   */
  public boolean isSuccess() {
    return errors.isEmpty();
  }

  /**
   * It returns all errors in the validation result.
   *
   * @return the list of error messages, empty list if there is no error
   */
  public List<String> getErrors() {
    return errors;
  }
}
