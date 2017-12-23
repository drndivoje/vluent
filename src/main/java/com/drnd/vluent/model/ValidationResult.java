package com.drnd.vluent.model;

import java.util.LinkedList;
import java.util.List;

public final class ValidationResult {
    private List<String> validationMessages = new LinkedList<>();
    public static ValidationResult SUCCESS = new ValidationResult();

    private void add(String message) {
        validationMessages.add(message);
    }

    public static ValidationResult createError(String messageKey) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(messageKey);
        return validationResult;
    }

    private void addAll(List<String> validationMessages) {
        this.validationMessages.addAll(validationMessages);
    }

    public boolean isSuccess() {
        return validationMessages.isEmpty();
    }

    public List<String> getValidationMessages() {
        return validationMessages;
    }

}


