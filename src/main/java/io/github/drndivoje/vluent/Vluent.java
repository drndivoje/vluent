package io.github.drndivoje.vluent;

import io.github.drndivoje.vluent.model.Precondition;
import io.github.drndivoje.vluent.model.ValidateWith;
import io.github.drndivoje.vluent.model.ValidationConverter;
import io.github.drndivoje.vluent.model.ValidationResult;
import io.github.drndivoje.vluent.model.Validator;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * The Entry point for creating validation chain. Adding validation steps does not invoke
 * validation. The validation is invoked calling method validate and validateAndConvert
 */
public final class Vluent {

  private final Chain chain;

  private Vluent() {
    this.chain = new Chain();
  }

  /**
   * Create a new instance of {@link Vluent}
   *
   * @return the instance of {@link Vluent}
   */
  public static Vluent create() {
    return new Vluent();
  }

  /**
   * Apply a validator to entity of type T. This method does not execute validation. It adds the
   * validator to internal validation chain.
   *
   * @param toValidate entity to validate
   * @param validator an instance of {@link Validator}
   * @param <T> type of the entity to validate
   * @return the instance of {@link Vluent} with updated validation chain.
   */
  public <T> Vluent on(T toValidate, Validator<T> validator) {
    chain.add(toValidate, validator);
    return this;
  }

  /**
   * Apply all validators defined inside the entity to validate using annotation {@link
   * ValidateWith}. This method does not execute validation. It adds the validator to internal
   * validation chain.
   *
   * @param toValidate entity to validate
   * @param <T> type of the entity to validate
   * @return the instance of {@link Vluent} with updated validation chain.
   */
  public <T> Vluent on(T toValidate) {
    Field[] declaredFields = toValidate.getClass().getDeclaredFields();
    for (Field field : declaredFields) {
      List<Validator<T>> validators = AnnotationResolution.resolveValidateWithAnnotation(field);
      if (!validators.isEmpty()) {
        T fieldValue = getFieldValue(field, toValidate);
        chain.addAll(fieldValue, validators);
      }
    }
    return this;
  }

  /**
   * Apply a validator to value provided by {@param valueSupplier}. This method does not execute
   * validation. It adds the validator to internal validation chain.
   *
   * @param valueSupplier the supplier of the value to validate
   * @param validator an instance of {@link Validator}
   * @param <T> the type of the value to validate
   * @return the instance of {@link Vluent} with updated validation chain.
   */
  public <T> Vluent on(Supplier<T> valueSupplier, Validator<T> validator) {
    chain.add(valueSupplier, validator);
    return this;
  }

  /**
   * Apply the precondition to the validation chain. If the precondition is met, the validation is
   * executed.
   *
   * @param precondition the precondition
   * @return the instance of {@link PreconditionValidation}
   */
  public PreconditionValidation when(Precondition precondition) {
    return new PreconditionValidation(precondition, this);
  }

  /**
   * Apply the validator to each value in the collection. This method does not execute validation.
   * It adds the validator to internal validation chain.
   *
   * @param values the collection of values to validate
   * @param validator an instance of {@link Validator}
   * @param <T> the type of the value to validate
   * @return the instance of {@link Vluent} with updated validation chain.
   */
  public <T> Vluent forEach(Collection<T> values, Validator<T> validator) {
    for (T value : values) {
      chain.add(value, validator);
    }
    return this;
  }

  /**
   * It validates the chain and returns the result
   *
   * @return the result of validation
   */
  public ValidationResult validate() {

    return chain.stream()
        .map(Step::execute)
        .filter(validationResult -> !validationResult.isSuccess())
        .findFirst()
        .orElse(ValidationResult.SUCCESS);
  }

  /**
   * It validates the chain and converts the result to the desired type using {@link
   * ValidationConverter}
   *
   * @param validationResultConverter the converter
   * @param <T> the type of the result
   * @return the converted result
   */
  public <T> T validateAndConvert(ValidationConverter<T> validationResultConverter) {
    return validationResultConverter.convert(this.validate());
  }

  /** The class to define precondition for validation */
  public static final class PreconditionValidation {

    private final Precondition precondition;
    private final Vluent sourceValidator;

    private PreconditionValidation(Precondition precondition, Vluent sourceValidator) {
      this.precondition = precondition;
      this.sourceValidator = sourceValidator;
    }

    /**
     * Apply a validator to entity of type T if precondition is satisfied. This method does not
     * execute validation. It adds the validator to internal validation chain.
     *
     * @param toValidate entity to validate
     * @param validator an instance of {@link Validator}
     * @param <T> type of the entity to validate
     * @return instance of {@link Vluent} with updated validation chain.
     */
    public <T> Vluent then(T toValidate, Validator<T> validator) {
      if (precondition.get()) {
        sourceValidator.on(toValidate, validator);
      }
      return sourceValidator;
    }

    /**
     * Apply validator to each value in the collection if precondition is satisfied.
     *
     * @param values the collection of values to validate
     * @param validator instance of {@link Validator}
     * @param <T> type of the entity to validate
     * @return instance of {@link Vluent} with updated validation chain.
     */
    public <T> PreconditionValidation thenForEach(Collection<T> values, Validator<T> validator) {
      if (precondition.get()) {
        sourceValidator.forEach(values, validator);
      }
      return this;
    }
  }

  private static <T> T getFieldValue(Field field, Object root) {
    field.setAccessible(true);
    try {
      return (T) field.get(root);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
