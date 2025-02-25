package io.github.drndivoje.vluent.example;

import io.github.drndivoje.vluent.model.ValidationResult;
import io.github.drndivoje.vluent.model.Validator;

/**
 * author: drndivoje
 */
public class EqualStringValidator implements Validator<String>{

    private final String referentString;

    public EqualStringValidator(String referentString) {
        this.referentString = referentString;
    }

    @Override
    public ValidationResult validate(String value) {
        if(referentString.equals(value)) return ValidationResult.SUCCESS;
        return ValidationResult.createError(value + "!=" + referentString);
    }
}
