package io.github.drndivoje.vluent;

import io.github.drndivoje.vluent.example.OlderThen18Validator;
import io.github.drndivoje.vluent.example.User;
import io.github.drndivoje.vluent.model.Validator;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationResolutionTest {

    @Test
    public void shouldReturnEmptyValidatorsListForNonAnnotatedField() {
        User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3);
        Field[] declaredFields = user.getClass().getDeclaredFields();
        List<Validator<Object>> validators = AnnotationResolution.resolveValidateWithAnnotation(declaredFields[0]);
        assertThat(validators.isEmpty());
    }

    @Test
    public void shouldValidatorsListForAnnotatedField() {
        User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3);
        Field[] declaredFields = user.getClass().getDeclaredFields();
        List<Validator<Object>> validators = AnnotationResolution.resolveValidateWithAnnotation(declaredFields[1]);
        assertThat(validators).hasOnlyElementsOfTypes(OlderThen18Validator.class).hasSize(1);
    }

}