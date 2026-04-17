# Vluent

Simple validation library for Java with zero dependencies. It provides easier way to implement
complex validation rules
by making it composable and easy to test.
It can be used for any tier of your application, and it is not tied to any specific framework.
Vluent is compatible with the Java Bean Validation Specification and can be used both as a standalone
validation library and as part of standard Bean Validation frameworks like Hibernate Validator.

## Usage

Add dependency to your project

``` xml
<dependency>
    <groupId>com.github.drndivoje</groupId>
    <artifactId>vluent</artifactId>
    <version>0.0.4</version>
</dependency>
```

For Bean Validation integration with Hibernate Validator, also add:

``` xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>9.1.0.Final</version>
</dependency>
```

## Standalone Usage

Every custom validator should implement _Validator_ interface and _validate_ method which should
return
ValidatitionResult instance

``` java
public class UserNameValidator implements Validator<String>{


    @Override
    public ValidationResult validate(String value) {
        if(value!==null) return SUCCESS;
        return ValidationResult.createError(value + " is null");
    }
```

ValidationResult is class which contains information on validation error as String.

### Applying Validation

For example if you have a User class

``` java

public class User {
    private final String name;
    private final LocalDate birthday;
    private final double salary;
    ...
```

Then to validate User instance call Vluent like

``` java

ValidationResult validationResult = Vluent.create()
    .on(user.getName(), new UserNameValidator())
    .on(user.getSalary(), new SalaryValidator())
    .validate();
```

The validators will apply sequentially. In the above example, if the username is invalid, the
validation for the salary
will not be executed, and the validation result will contain only information about the invalid
username.

### Validation with Preconditions

In cases of complex validation rules, preconditions could be defined before applying validator. Each
precondition should
implement
Precondition interface.

``` java

ValidationResult validationResult = Vluent.create()
    .on(user.getName(), new UserNameValidator())
    .when(() -> user.getSalary() > 1000 )
    .then(user.getSalary(), new SalaryValidator())
    .validate();
```

In this case SalaryValidator will be invoked only for salaries greater then 1000. Unit testing each
rules is easy as
they are independent of each other.

### Custom Validation Rules

If we want to validate that only users born before 1970 and named Bob must have salary greater then
2000, we can define
validator and precondition like

``` java
Precondition agePrecondition = () -> user.birthday().isBefore(LocalDate.of(1970, 1, 1));
Precondition namePrecondition = () -> user.name().equals("Bob");
Validator<Double> salaryValidator = (value) -> {
       if (value > 2000) return ValidationResult.SUCCESS;
            return ValidationResult.createError("not enough money");
        };
```

And then apply them like

``` java
ValidationResult validationResult = Vluent.create()
    .on(user.getName(), new UserNameValidator())
    .when(agePrecondition.and(namePrecondition))
    .then(user.getSalary(), salaryValidator)
    .validate();
```

The same validators and preconditions can be reused in different validation rules. For example,
if we want to validate that that only users born after 1970 or named Bob must not have salary
greater then 2000

``` java
ValidationResult validationResult = Vluent.create()
 .when(agePrecondition.not().or(namePrecondition))
 .then(user.salary(), salaryValidator.invert())
 .validate();
```

## Bean Validation Integration

Vluent can be seamlessly integrated with the Java Bean Validation specification (Jakarta Validation)
and used with Hibernate Validator. This allows you to use your Vluent validators as part of the 
standard Bean Validation framework.

### Using @ValidateBeanWith Annotation

You can annotate your beans with `@ValidateBeanWith` to apply Vluent validators as constraint validators:

``` java
@ValidateBeanWith(UserValidator.class)
public record User(
    @NotBlank String name,
    LocalDate birthDate,
    double salary) {}
```

Your Vluent validator remains the same, implementing the standard Vluent `Validator` interface:

``` java
public class UserValidator implements Validator<User> {

  @Override
  public ValidationResult validate(User value) {
    LocalDate now = LocalDate.now();
    LocalDate birthDate = value.birthDate();
    long between = ChronoUnit.YEARS.between(birthDate, now);
    if (between >= 18) {
      return ValidationResult.SUCCESS;
    } else {
      return ValidationResult.createError("too young");
    }
  }
}
```

### Benefits of Dual Mode Support

- **Flexibility**: Use the same validators in both standalone Vluent API calls and as part of Bean Validation
- **Framework Integration**: Seamlessly integrate with Spring Boot validation, JAX-RS validation, and other frameworks that support Bean Validation
- **Mix and Match**: Combine Vluent validators with standard Bean Validation constraints like `@NotBlank`, `@Size`, etc.
- **No Additional Code**: Your existing Vluent validators work with Bean Validation without modification

### Example with Spring Boot

In a Spring Boot REST controller, Bean Validation with Vluent validators works automatically:

``` java
@RestController
public class UserController {

  @PostMapping("/users")
  public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    // If validation fails, Spring Boot automatically returns 400 Bad Request
    return ResponseEntity.ok(user);
  }
}
```

The same `UserValidator` can also be used explicitly with the Vluent API:

``` java
ValidationResult result = new UserValidator().validate(user);
if (!result.isSuccess()) {
  // Handle validation error
}
```
