package com.drndivoje.vluent;

import com.drndivoje.vluent.model.ValidateWith;
import com.drndivoje.vluent.model.Validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

class AnnotationResolution {

    static <T> List<Validator<T>> resolveValidateWithAnnotation(Field field) {


        ValidateWith validateWithAnnotation = field.getAnnotation(ValidateWith.class);
        if (validateWithAnnotation == null) {
            return emptyList();
        }
        Class<? extends Validator>[] validatorClasses = validateWithAnnotation.value();
        List<Validator<T>> validators = new ArrayList<>(validatorClasses.length);
        for (Class<? extends Validator> validatorClass : validatorClasses) {
            validators.add(createValidatorInstance(validatorClass));
        }
        return validators;
    }

    private static <T> Validator<T> createValidatorInstance(Class<? extends Validator> validatorClass) {
        Constructor<?>[] constructors = validatorClass.getConstructors();
        for (Constructor constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                try {
                    return (Validator<T>) constructor.newInstance();
                } catch (InstantiationException e) {
                    throw new IllegalStateException("Cannon create validator insincae from " + validatorClass.getCanonicalName(), e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Cannon access constructor of  validator class " + validatorClass.getCanonicalName(), e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Failed to create validator instance of " + validatorClass.getCanonicalName(), e);
                }
            }
        }
        throw new IllegalStateException("Could not find default constructor for " + validatorClass.getCanonicalName());
    }

}
