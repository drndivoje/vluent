package com.drnd.vluent.model;

public interface Validator<T> {
    ValidationResult validate(T value);
}
