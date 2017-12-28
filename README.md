# Vluent

Simple validation framework using fluent interface to define complex validation rules

## Usage

Every custom validator should implement Validator interface and implement validate method which should return ValidatitionResult instance

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
The simplest use case is to validate the typical pojo
if there is User class
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
 The validators will apply sequentially. Taking the above example, it means if username is invalid the validation for salary will not be executed and Validation result will contains information on invalid username only

### Validation Preconditions

In cases of complex validation rules, preconditions could be defined before applying validator

``` java

ValidationResult validationResult = Vluent.create()
    .on(user.getName(), new UserNameValidator())
    .when(() -> user.getSalary() > 1000 )
    .then(user.getSalary(), new SalaryValidator())
    .validate();
```
In this case SalaryValidator will be invoke oly for salaries greater then 1000
