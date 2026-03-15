package io.github.drndivoje.vluent.example;

import io.github.drndivoje.vluent.model.ValidateBeanWith;
import io.github.drndivoje.vluent.model.ValidationResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HibernateValidatorTest {

  @ValidateBeanWith(value = TestObjectValidator.class)
  public record TestObject(String firstName, LocalDate birthdate) {}

  public static class TestObjectValidator
      implements io.github.drndivoje.vluent.model.Validator<TestObject> {

    public TestObjectValidator() {}

    @Override
    public ValidationResult validate(TestObject value) {
      if (value.firstName.startsWith("A")) {
        return ValidationResult.SUCCESS;
      }
      return ValidationResult.createError("Does not start with A");
    }
  }

  private Validator validator;

  @BeforeClass
  public void setUp() {
    try (ValidatorFactory factory =
        Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  public void testWithHibernateValidator() {
    // Invalid case - does not start with A
    TestObject underAge = new TestObject("John", LocalDate.now().minusYears(15));
    Set<ConstraintViolation<TestObject>> violations = validator.validate(underAge);
    Assert.assertFalse(violations.isEmpty(), "Should have violations for under-age person");

    // Valid case - Start with A
    TestObject adult = new TestObject("Albert", LocalDate.now().minusYears(20));
    violations = validator.validate(adult);
    Assert.assertTrue(violations.isEmpty(), "Should have no violations for adult person");
  }
}
