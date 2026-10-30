package io.github.drndivoje.vluent.internal.beanvalidation;

import io.github.drndivoje.vluent.api.ValidateBeanWith;
import io.github.drndivoje.vluent.api.ValidationResult;
import io.github.drndivoje.vluent.api.Validator;
import io.github.drndivoje.vluent.internal.Util;
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
    boolean isValid = true;
    for (Validator<Object> validator : validators) {
      ValidationResult validationResult = validator.validate(value);
      if (!validationResult.isSuccess()) {
        // report Vluent's own messages instead of the generic ValidateBeanWith message
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(validationResult.getErrorMessage())
            .addConstraintViolation();
        isValid = false;
      }
    }
    return isValid;
  }
}
