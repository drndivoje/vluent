package io.github.drndivoje.vluent.example;

import io.github.drndivoje.vluent.model.ValidationResult;
import io.github.drndivoje.vluent.model.Validator;

public class InvalidConstructorValidator implements Validator<String> {
  public InvalidConstructorValidator() {
    throw new RuntimeException("Invalid constructor");
  }

  @Override
  public ValidationResult validate(String value) {
    return null;
  }
}
