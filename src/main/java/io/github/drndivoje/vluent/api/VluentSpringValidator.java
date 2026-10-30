package io.github.drndivoje.vluent.api;

import org.springframework.validation.Errors;

/**
 * Adapter which exposes a Vluent {@link Validator} as a Spring {@link
 * org.springframework.validation.Validator}. It can be registered on a {@code WebDataBinder} (e.g.
 * in an {@code @InitBinder} method) so that the Vluent validator is executed for {@code @Valid}
 * arguments.
 *
 * <p>Spring is an optional dependency. It is only needed when this class is used.
 *
 * @param <T> the type of the validated object
 */
public final class VluentSpringValidator<T> implements org.springframework.validation.Validator {

  /** The default error code used when validation fails */
  public static final String DEFAULT_ERROR_CODE = "Vluent";

  private final Class<T> type;
  private final Validator<T> validator;
  private final String errorCode;

  /**
   * It creates the adapter which reports failures with {@link #DEFAULT_ERROR_CODE}
   *
   * @param type the type of the validated object
   * @param validator the Vluent validator
   */
  public VluentSpringValidator(Class<T> type, Validator<T> validator) {
    this(type, validator, DEFAULT_ERROR_CODE);
  }

  /**
   * It creates the adapter which reports failures with the given error code. The code is resolved
   * by Spring's {@code MessageCodesResolver}, so it can be used as a key in {@code
   * messages.properties}.
   *
   * @param type the type of the validated object
   * @param validator the Vluent validator
   * @param errorCode the error code of the reported error
   */
  public VluentSpringValidator(Class<T> type, Validator<T> validator, String errorCode) {
    this.type = type;
    this.validator = validator;
    this.errorCode = errorCode;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return type.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationResult result = validator.validate(type.cast(target));
    if (!result.isSuccess()) {
      errors.reject(errorCode, result.getErrorMessage());
    }
  }
}
