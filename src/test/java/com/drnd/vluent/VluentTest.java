package com.drnd.vluent;

import com.drnd.vluent.example.EqualStringValidator;
import com.drnd.vluent.example.OlderThen18Validator;
import com.drnd.vluent.example.User;
import com.drnd.vluent.model.ValidationResult;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * author: drndivoje
 */
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
        assertThat(validationResult1.getValidationMessages()).hasSize(1).contains("AString!=aSrtring");
    }

    @Test
    public void shouldNotExecuteValidationWithPrecondition() {
        ValidationResult validationResult1 = Vluent.create().when(() -> false).then("AString", new EqualStringValidator("aSrtring")).validate();
        assertThat(validationResult1.isSuccess()).isTrue();
    }

    @Test
    public void shouldValidateSimpleValidationChain() {
        User user = new User("Bob", LocalDate.of(1960, 3, 3), 2303.3);
        ValidationResult validationResult = Vluent.create().on(user.getName(), new EqualStringValidator("Bob"))
                .on(user.getBirthday(), new OlderThen18Validator())
                .on(user.getSalary(), (value) -> {
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
        assertThat(booleanResult.getValidationMessages()).contains("1!=one");

    }

}