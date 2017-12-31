package com.drnd.vluent.example;

import com.drnd.vluent.model.ValidationResult;
import com.drnd.vluent.model.Validator;

import static com.drnd.vluent.model.ValidationResult.SUCCESS;

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
        if(referentString.equals(value)) return SUCCESS;
        return ValidationResult.createError(value + "!=" + referentString);
    }
}
