_# Vluent

Simple validation library for Java with zero dependencies. It provides easier way to implement complex validation rules
by making it composable and easy to test.
It can be used for any tier of your application, and it is not tied to any specific framework.
The Vluent is not compatible with the Java Bean Validation Specification, and it follows similar approach like Spring
Validator interface.

## Usage

Every custom validator should implement _Validator_ interface and _validate_ method which should return
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

The validators will apply sequentially. In the above example, if the username is invalid, the validation for the salary
will not be executed, and the validation result will contain only information about the invalid username.

### Validation with Preconditions

In cases of complex validation rules, preconditions could be defined before applying validator. Each precondition should
implement
Precondition interface.

``` java

ValidationResult validationResult = Vluent.create()
    .on(user.getName(), new UserNameValidator())
    .when(() -> user.getSalary() > 1000 )
    .then(user.getSalary(), new SalaryValidator())
    .validate();
```

In this case SalaryValidator will be invoked only for salaries greater then 1000. Unit testing each rules is easy as
they are independent of each other.

### Custom Validation Rules

If we want to validate that only users born before 1970 and named Bob must have salary greater then 2000, we can define
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
if we want to validate that that only users born after 1970 or named Bob must not have salary greater then 2000

``` java
ValidationResult validationResult = Vluent.create()
 .when(agePrecondition.not().or(namePrecondition))
 .then(user.salary(), salaryValidator.invert())
 .validate();
```
