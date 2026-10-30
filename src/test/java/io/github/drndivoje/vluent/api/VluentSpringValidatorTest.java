package io.github.drndivoje.vluent.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drndivoje.vluent.example.OlderThen18Validator;
import io.github.drndivoje.vluent.example.User;
import java.time.LocalDate;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.testng.annotations.Test;

public class VluentSpringValidatorTest {

  private static final Validator<User> ADULT =
      user -> new OlderThen18Validator().validate(user.birthday());

  @Test
  public void shouldSupportValidatedTypeOnly() {
    VluentSpringValidator<User> validator = new VluentSpringValidator<>(User.class, ADULT);
    assertThat(validator.supports(User.class)).isTrue();
    assertThat(validator.supports(String.class)).isFalse();
  }

  @Test
  public void shouldNotRejectValidObject() {
    User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3, "Street");
    Errors errors = new BeanPropertyBindingResult(user, "user");

    new VluentSpringValidator<>(User.class, ADULT).validate(user, errors);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  public void shouldRejectInvalidObjectWithDefaultCode() {
    User user = new User("Bob", LocalDate.now(), 2303.3, "Street");
    Errors errors = new BeanPropertyBindingResult(user, "user");

    new VluentSpringValidator<>(User.class, ADULT).validate(user, errors);

    assertThat(errors.getErrorCount()).isEqualTo(1);
    ObjectError error = errors.getGlobalError();
    assertThat(error.getDefaultMessage()).isEqualTo("to young");
    assertThat(error.getCodes()).containsExactly("Vluent.user", "Vluent");
    assertThat(error.getArguments()).isNullOrEmpty();
  }

  @Test
  public void shouldRejectInvalidObjectWithCustomCode() {
    User user = new User("Bob", LocalDate.now(), 2303.3, "Street");
    Errors errors = new BeanPropertyBindingResult(user, "user");

    new VluentSpringValidator<>(User.class, ADULT, "user.too.young").validate(user, errors);

    assertThat(errors.getGlobalError().getCodes()).contains("user.too.young");
  }
}
