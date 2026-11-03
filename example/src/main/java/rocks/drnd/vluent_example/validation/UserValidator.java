package rocks.drnd.vluent_example.validation;

import io.github.drndivoje.vluent.api.ValidationResult;
import io.github.drndivoje.vluent.api.Validator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import rocks.drnd.vluent_example.dto.User;

public class UserValidator implements Validator<User> {

  @Override
  public ValidationResult validate(User value) {
    LocalDate now = LocalDate.now();
    LocalDate birthDate = value.birthDate();
    long between = ChronoUnit.YEARS.between(birthDate, now);
    if (between >= 18) {
      return ValidationResult.SUCCESS;
    } else {
      return ValidationResult.createError("The user: " + value + " is too young");
    }
  }
}
