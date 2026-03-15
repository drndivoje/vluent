package io.github.drndivoje.vluent;

import io.github.drndivoje.vluent.model.Validator;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class Util {
  public static <T> Validator<T> createValidatorInstance(
      Class<? extends Validator> validatorClass) {
    Constructor<?>[] constructors = validatorClass.getConstructors();
    for (Constructor<?> constructor : constructors) {
      if (constructor.getParameterCount() == 0) {
        try {
          return (Validator<T>) constructor.newInstance();
        } catch (InstantiationException e) {
          throw new IllegalStateException(
              "Cannon create validator instance from " + validatorClass.getCanonicalName(), e);
        } catch (IllegalAccessException e) {
          throw new IllegalStateException(
              "Cannon access constructor of  validator class " + validatorClass.getCanonicalName(),
              e);
        } catch (InvocationTargetException e) {
          throw new IllegalStateException(
              "Failed to create validator instance of " + validatorClass.getCanonicalName(), e);
        }
      }
    }
    throw new IllegalStateException(
        "Could not find default constructor for " + validatorClass.getCanonicalName());
  }
}
