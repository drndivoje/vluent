package io.github.drndivoje.vluent;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drndivoje.vluent.example.OlderThen18Validator;
import io.github.drndivoje.vluent.example.User;
import io.github.drndivoje.vluent.model.Validator;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.testng.annotations.Test;

public class AnnotationResolutionTest {

  @Test
  public void shouldReturnEmptyValidatorsListForNonAnnotatedField() {
    User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3);
    Field[] declaredFields = user.getClass().getDeclaredFields();
    List<Validator<Object>> validators =
        AnnotationResolution.resolveValidateWithAnnotation(declaredFields[0]);
    assertThat(validators.isEmpty()).isTrue();
  }

  @Test
  public void shouldValidatorsListForAnnotatedField() {
    User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3);
    Field[] declaredFields = user.getClass().getDeclaredFields();
    List<Validator<Object>> validators =
        AnnotationResolution.resolveValidateWithAnnotation(declaredFields[1]);
    assertThat(validators).hasOnlyElementsOfTypes(OlderThen18Validator.class).hasSize(1);
  }
}
