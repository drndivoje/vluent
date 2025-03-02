package io.github.drndivoje.vluent;

import io.github.drndivoje.vluent.example.EqualStringValidator;
import io.github.drndivoje.vluent.example.OlderThen18Validator;
import io.github.drndivoje.vluent.example.User;
import io.github.drndivoje.vluent.model.Precondition;
import io.github.drndivoje.vluent.model.ValidationResult;
import io.github.drndivoje.vluent.model.Validator;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class VluentTest {

    @Test
    public void shouldCreateVluentInstance() {
        assertThat(Vluent.create()).isNotNull();
    }

    @Test
    public void shouldExecuteSimpleValidation() {
        ValidationResult validationResult = Vluent.create().on(true, value -> ValidationResult.SUCCESS).validate();
        assertThat(validationResult.isSuccess()).isTrue();
    }

    @Test
    public void shouldExecuteValidationWithPrecondition() {
        ValidationResult validationResult1 = Vluent.create().when(() -> true).then("AString", new EqualStringValidator("aSrtring")).validate();
        assertThat(validationResult1.isSuccess()).isFalse();
        assertThat(validationResult1.getInvalidationMessages()).hasSize(1).contains("AString!=aSrtring");
    }

    @Test
    public void shouldValidateWithComplexPreconditions() {
        User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3);
        //validate that only users born before 1970 and named Bob must have salary greater then 2000
        Precondition agePrecondition = () -> user.birthday().isBefore(LocalDate.of(1970, 1, 1));
        Precondition namePrecondition = () -> user.name().equals("Bob");
        Validator<Double> salaryValidator = (value) -> {
            if (value > 2000) return ValidationResult.SUCCESS;
            return ValidationResult.createError("not enough money");
        };
        ValidationResult validationResult = Vluent.create().when(agePrecondition.and(namePrecondition))
                .then(user.salary(), salaryValidator).validate();
        assertThat(validationResult.isSuccess()).isTrue();
       //validate that only users born after 1970 or named Bob must not have salary greater then 2000
        ValidationResult validationResult2 = Vluent.create()
                .when(agePrecondition.not().or(namePrecondition))
                .then(user.salary(), salaryValidator.invert()).validate();
        assertThat(validationResult2.isSuccess()).isTrue();

    }

    @Test
    public void shouldNotExecuteValidationWithPrecondition() {
        ValidationResult validationResult1 = Vluent.create().when(() -> false).then("AString", new EqualStringValidator("aSrtring")).validate();
        assertThat(validationResult1.isSuccess()).isTrue();
    }

    @Test
    public void shouldValidateSimpleValidationChain() {
        User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3);
        ValidationResult validationResult = Vluent.create().on(user.name(), new EqualStringValidator("Bob"))
                .on(user.birthday(), new OlderThen18Validator())
                .on(user.salary(), (value) -> {
                    if (value > 2000) return ValidationResult.SUCCESS;
                    return ValidationResult.createError("poor");
                }).validate();

        assertThat(validationResult.isSuccess()).isTrue();

    }

    @Test
    public void shouldValidateAndConvert() {
        boolean booleanResult = Vluent.create().on("AString", new EqualStringValidator("aSrtring"))
                .validateAndConvert(ValidationResult::isSuccess);
        assertThat(booleanResult).isFalse();

    }

    @Test
    public void shouldValidateCollectionOfValues() {
        ValidationResult booleanResult = Vluent.create().forEach(Arrays.asList("one", "one", "1"), new EqualStringValidator("one")).validate();
        assertThat(booleanResult.getInvalidationMessages()).contains("1!=one");

    }

    @Test
    public void shouldValidateAnnotatedField() {
        User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3);

        ValidationResult validationResult = Vluent.create().on(user).validate();
        assertThat(validationResult.isSuccess()).isTrue();
    }

}