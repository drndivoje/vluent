package io.github.drndivoje.vluent.beanvalidation;

import io.github.drndivoje.vluent.Util;
import io.github.drndivoje.vluent.model.ValidateBeanWith;
import io.github.drndivoje.vluent.model.ValidationResult;
import io.github.drndivoje.vluent.model.Validator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * Jakarta Bean Validation constraint validator implementation for {@link ValidateBeanWith}
 * annotation.
 *
 * <p>This validator executes all configured Vluent validators and returns success only if all of
 * them pass validation.
 */
public class VluentJakartaBeanValidator implements ConstraintValidator<ValidateBeanWith, Object> {
  private List<Validator<Object>> validators;

  /** Default constructor. */
  public VluentJakartaBeanValidator() {}

  @Override
  public void initialize(ValidateBeanWith constraintAnnotation) {
    Class<? extends Validator>[] validatorClasses = constraintAnnotation.value();
    this.validators =
        Arrays.stream(validatorClasses).sequential().map(Util::createValidatorInstance).toList();

    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    return validators.stream()
        .map(validator -> validator.validate(value))
        .allMatch(ValidationResult::isSuccess);
  }
}
