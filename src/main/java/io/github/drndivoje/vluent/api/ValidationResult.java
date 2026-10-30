package io.github.drndivoje.vluent.api;

/**
 * The result of validation returned by {@link Validator}. It contains the invalidation messages if
 * the case of failed validation.
 */
public final class ValidationResult {

  private String errorMessage;

  private ValidationResult() {}

  /** The successful validation result */
  public static final ValidationResult SUCCESS = new ValidationResult();

  private void add(String message) {
    this.errorMessage = message;
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
    return errorMessage == null;
  }

  /**
   * It returns the error message
   *
   * @return the error message, null if there is no error message
   */
  public String getErrorMessage() {
    return errorMessage;
  }
}
