package com.drnd.vluent.model;

public interface ValidationConverter<T> {
    T convert(ValidationResult result);
}
