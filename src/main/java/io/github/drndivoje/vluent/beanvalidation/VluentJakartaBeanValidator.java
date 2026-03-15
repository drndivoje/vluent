package io.github.drndivoje.vluent.beanvalidation;

import io.github.drndivoje.vluent.Util;
import io.github.drndivoje.vluent.model.ValidateBeanWith;
import io.github.drndivoje.vluent.model.ValidationResult;
import io.github.drndivoje.vluent.model.Validator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class VluentJakartaBeanValidator implements ConstraintValidator<ValidateBeanWith, Object> {
  private List<Validator<Object>> validators;

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
