package io.github.drndivoje.vluent.example;

import io.github.drndivoje.vluent.model.ValidationResult;
import io.github.drndivoje.vluent.model.Validator;

public class PrivateConstructorValidator implements Validator<String> {

  private PrivateConstructorValidator() {}

  @Override
  public ValidationResult validate(String value) {
    return null;
  }
}
