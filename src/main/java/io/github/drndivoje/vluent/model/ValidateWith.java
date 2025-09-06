package io.github.drndivoje.vluent.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** It is used to define multiple validators for a single field. */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateWith {

  /**
   * It defines multiple validators for a single field.
   *
   * @return the array of validators
   */
  Class<? extends Validator>[] value() default {};
}
