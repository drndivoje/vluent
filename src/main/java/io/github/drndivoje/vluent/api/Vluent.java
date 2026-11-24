package io.github.drndivoje.vluent.api;

import io.github.drndivoje.vluent.internal.AnnotationResolution;
import io.github.drndivoje.vluent.internal.Chain;
import io.github.drndivoje.vluent.internal.Step;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
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
   * Create a new instance of {@link ChainedValidator}
   *
   * @return the instance of {@link ChainedValidator}
   */
  public static ChainedValidator createValidation() {
    return new ChainedValidator();
  }

  /**
   * Apply all validators defined inside the entity to validate using annotation {@link
   * ValidateWith}. This method does not execute validation. It adds the validator to internal
   * validation chain.
   *
   * @param toValidate entity to validate
   * @param <T> type of the entity to validate
   * @return the instance of {@link ChainedValidator} with updated validation chain.
   */
  public static <T> ChainedValidator scan(T toValidate) {
    ChainedValidator chainedValidator = new ChainedValidator();
    Field[] declaredFields = toValidate.getClass().getDeclaredFields();
    for (Field field : declaredFields) {
      List<Validator<T>> validators = AnnotationResolution.resolveValidateWithAnnotation(field);
      if (!validators.isEmpty()) {
        T fieldValue = getFieldValue(field, toValidate);
        chainedValidator.addValidatorsToValue(fieldValue, validators);
      }
    }
    return chainedValidator;
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
    List<String> errors =
        chain.stream()
            .map(Step::execute)
            .filter(validationResult -> !validationResult.isSuccess())
            .flatMap(validationResult -> validationResult.getErrors().stream())
            .toList();
    if (errors.isEmpty()) {
      return ValidationResult.SUCCESS;
    }
    return ValidationResult.createError(errors);
  }

  /** The class to define precondition for validation */
  public static final class PreconditionValidation {

    private final Precondition precondition;
    private final ChainedValidator sourceValidator;

    private PreconditionValidation(Precondition precondition, ChainedValidator sourceValidator) {
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
     * @return instance of {@link ChainedValidator} with updated validation chain.
     */
    public <T> ChainedValidator then(T toValidate, Validator<T> validator) {
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
    public <T> ChainedValidator thenForEach(Collection<T> values, Validator<T> validator) {
      if (precondition.get()) {
        sourceValidator.applyValidatorToEach(values, validator);
      }
      return sourceValidator;
    }
  }

  /** A class to build a chain of validators and execute them. */
  public static class ChainedValidator {
    private final Chain chain;

    private ChainedValidator() {
      this.chain = new Chain();
    }

    private <T> void addValidator(T value, Validator<T> validator) {
      chain.add(value, validator);
    }

    /**
     * Add multiple validators to a single field value.
     *
     * @param fieldValue the value to validate
     * @param validators the list of validators to apply
     * @param <T> the type of the value to validate
     */
    public <T> void addValidatorsToValue(T fieldValue, List<Validator<T>> validators) {
      for (Validator<T> validator : validators) {
        this.addValidator(fieldValue, validator);
      }
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
     * It validates the chain and returns the result
     *
     * @return the result of validation
     */
    public ValidationResult validate() {
      List<String> errors =
          chain.stream()
              .map(Step::execute)
              .filter(validationResult -> !validationResult.isSuccess())
              .flatMap(validationResult -> validationResult.getErrors().stream())
              .toList();
      if (errors.isEmpty()) {
        return ValidationResult.SUCCESS;
      }
      return ValidationResult.createError(errors);
    }

    /**
     * Apply a validator to a value. This method does not execute validation. It adds the validator
     * to internal validation chain.
     *
     * @param value the value to validate
     * @param validator an instance of {@link Validator}
     * @return the instance of {@link ChainedValidator} with updated validation chain.
     * @param <T> the type of the value to validate
     */
    public <T> ChainedValidator on(T value, Validator<T> validator) {
      chain.add(value, validator);
      return this;
    }

    /**
     * Apply a validator to value provided by {@param valueSupplier}. This method does not execute
     * validation. It adds the validator to internal validation chain.
     *
     * @param valueSupplier the supplier of the value to validate
     * @param validator an instance of {@link Validator}
     * @param <T> the type of the value to validate
     * @return the instance of {@link ChainedValidator} with updated validation chain.
     */
    public <T> ChainedValidator on(Supplier<T> valueSupplier, Validator<T> validator) {
      chain.add(valueSupplier, validator);
      return this;
    }

    /**
     * Apply a validator to each value in the collection. This method does not execute validation.
     * It adds the validator to internal validation chain.
     *
     * @param values the collection of values to validate
     * @param validator an instance of {@link Validator}
     * @param <T> the type of the values to validate
     * @return the instance of {@link ChainedValidator} with updated validation chain.
     */
    public <T> ChainedValidator applyValidatorToEach(Collection<T> values, Validator<T> validator) {
      for (T value : values) {
        this.addValidator(value, validator);
      }
      return this;
    }

    /**
     * It validates the chain and converts the result to the desired type
     *
     * @param validationResultConverter the converter
     * @param <T> the type of the result
     * @return the converted result
     */
    public <T> T validateAndConvert(Function<ValidationResult, T> validationResultConverter) {
      return validationResultConverter.apply(this.validate());
    }
  }

  private static <R> R getFieldValue(Field field, Object root) {
    field.setAccessible(true);
    try {
      return (R) field.get(root);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
