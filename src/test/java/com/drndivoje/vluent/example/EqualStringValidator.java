package com.drndivoje.vluent.example;

import com.drndivoje.vluent.model.ValidationResult;
import com.drndivoje.vluent.model.Validator;

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
