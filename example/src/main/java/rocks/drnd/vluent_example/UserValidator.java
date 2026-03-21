package rocks.drnd.vluent_example;

import io.github.drndivoje.vluent.model.ValidationResult;
import io.github.drndivoje.vluent.model.Validator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UserValidator implements Validator<User> {

  @Override
  public ValidationResult validate(User value) {
    LocalDate now = LocalDate.now();
    LocalDate birthDate = value.birthDate();
    long between = ChronoUnit.YEARS.between(birthDate, now);
    if (between >= 18) {
      return ValidationResult.SUCCESS;
    } else {
      return ValidationResult.createError("to young");
    }
  }
}
