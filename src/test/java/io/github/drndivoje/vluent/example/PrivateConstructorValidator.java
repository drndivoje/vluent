package io.github.drndivoje.vluent.example;

import io.github.drndivoje.vluent.api.ValidationResult;
import io.github.drndivoje.vluent.api.Validator;

public class PrivateConstructorValidator implements Validator<String> {

  private PrivateConstructorValidator() {}

  @Override
  public ValidationResult validate(String value) {
    return null;
  }
}
