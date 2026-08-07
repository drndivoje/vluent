package io.github.drndivoje.vluent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
    User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3, "Street");
    Field[] declaredFields = user.getClass().getDeclaredFields();
    List<Validator<Object>> validators =
        AnnotationResolution.resolveValidateWithAnnotation(declaredFields[2]);
    assertThat(validators.isEmpty()).isTrue();
  }

  @Test
  public void shouldValidatorsListForAnnotatedField() {
    User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3, "Street");
    Field[] declaredFields = user.getClass().getDeclaredFields();
    List<Validator<Object>> validators =
        AnnotationResolution.resolveValidateWithAnnotation(declaredFields[1]);
    assertThat(validators).hasOnlyElementsOfTypes(OlderThen18Validator.class).hasSize(1);
  }

  @Test
  public void shouldThrowIllegalStateExceptionWhenConstructorThrowsException() {
    User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3, "Street");
    Field[] declaredFields = user.getClass().getDeclaredFields();
    assertThatThrownBy(() -> AnnotationResolution.resolveValidateWithAnnotation(declaredFields[0]))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(
            "Failed to create validator instance of io.github.drndivoje.vluent.example.InvalidConstructorValidato");
  }

  @Test
  public void shouldThrowIllegalStateExceptionWhenConstructorIsPrivate() {
    User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3, "Street");
    Field[] declaredFields = user.getClass().getDeclaredFields();
    assertThatThrownBy(() -> AnnotationResolution.resolveValidateWithAnnotation(declaredFields[3]))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(
            "Could not find default constructor for io.github.drndivoje.vluent.example.PrivateConstructorValidator");
  }
}
