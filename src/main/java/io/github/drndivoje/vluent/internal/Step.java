package io.github.drndivoje.vluent.internal;

import io.github.drndivoje.vluent.api.ValidationResult;
import io.github.drndivoje.vluent.api.Validator;
import java.util.function.Supplier;

/**
 * Single validation step in validation chain. It executes validation on the value using correlated
 * validator
 *
 * @param <T> type of value to validate
 */
public class Step<T> {

  private final Validator<T> validator;
  private final Supplier<T> supplier;

  Step(Validator<T> validator, Supplier<T> supplier) {
    this.validator = validator;
    this.supplier = supplier;
  }

  public ValidationResult execute() {
    try {
      return validator.validate(supplier.get());
    } catch (Exception e) {
      return ValidationResult.createError(e.getMessage());
    }
  }

  record StepValidationResult(ValidationResult validationResult) {}
}
