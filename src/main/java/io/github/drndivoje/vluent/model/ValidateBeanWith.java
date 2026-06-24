package io.github.drndivoje.vluent.model;

import io.github.drndivoje.vluent.beanvalidation.VluentJakartaBeanValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a constraint annotation according to Java Bean Validation Specification. It is used as a
 * custom constraint to ake Vluent compatible with the JSR 303
 */
@Documented
@Constraint(validatedBy = VluentJakartaBeanValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateBeanWith {
  /**
   * It represents error message as a string
   *
   * @return the validation message
   */
  String message() default "Vluent Validation failed";

  /**
   * A groups element that specifies the processing groups with which the constraint declaration is
   * associated.
   *
   * @return the validation groups
   */
  Class<?>[] groups() default {};

  /**
   * A payload element that specifies the payload with which the the constraint declaration is
   * associated.
   *
   * @return the constraint payload
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * Chain of Vluent validators
   *
   * @return the validators to run for this bean
   */
  Class<? extends Validator>[] value() default {};
}
