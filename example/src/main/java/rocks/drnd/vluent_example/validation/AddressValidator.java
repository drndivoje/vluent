package rocks.drnd.vluent_example.validation;

import io.github.drndivoje.vluent.api.Precondition;
import io.github.drndivoje.vluent.api.ValidationResult;
import io.github.drndivoje.vluent.api.Validator;
import io.github.drndivoje.vluent.api.Vluent;
import rocks.drnd.vluent_example.dto.Address;

public class AddressValidator implements Validator<Address> {

  static final Validator<String> ZIP_CODE_PRESENT =
      zipCode ->
          zipCode != null && !zipCode.isBlank()
              ? ValidationResult.SUCCESS
              : ValidationResult.createError("zip code is required");

  static final Validator<String> GERMAN_ZIP_CODE =
      zipCode ->
          zipCode.matches("\\d{5}")
              ? ValidationResult.SUCCESS
              : ValidationResult.createError("German zip code must have 5 digits");

  @Override
  public ValidationResult validate(Address address) {
    Precondition isGermany = () -> "DE".equals(address.country());

    return Vluent.create()
        .on(address.zipCode(), ZIP_CODE_PRESENT)
        .when(isGermany)
        .then(address.zipCode(), GERMAN_ZIP_CODE)
        .validate();
  }
}
