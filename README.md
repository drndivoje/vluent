# Vluent

[![Maven Central](https://img.shields.io/maven-central/v/io.github.drndivoje/vluent?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.drndivoje/vluent)
[![License: MIT](https://img.shields.io/badge/license-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)

> Composable, testable Java validation rules — without annotation hell or framework coupling.

---

## The problem

Real business rules are conditional, cross-field, and context-dependent.
They don't fit annotations — they end up as untestable, unreusable if/else chains:

```java
if ("SELF_EMPLOYED".equals(app.employmentType()) && app.annualIncome() < 40_000) {
    return error("Self-employed income too low");
}
if (app.requestedAmount() > 100_000 && !app.isExistingCustomer() && app.creditScore() < 750) {
    return error("Large loan requires excellent credit");
}
```

You can not test these rules individually. You can not reuse them elsewhere.
Adding a new rule means finding the right place and hoping ordering still holds.

## The solution

Making validation rules first-class objects, and composing them with a fluent API:
```java
return Vluent.create()
    .on(app.name(), NAME_PRESENT)
    .on(app.age(), LEGAL_AGE)
    .when(isSelfEmployed)
    .then(app, SELF_EMPLOYED_INCOME)
    .when(isLargeLoan.and(isExistingCustomer.not()))
    .then(app.creditScore(), EXCELLENT_CREDIT)
    .validate();
```

Reads like a policy document. Every rule is a named object.
Every condition composes with `.and()`, `.or()`, `.not()`.
Every validator tests in one line — no full object construction, no ordering concerns.

Can be applied to the bean or field using annotations, or used standalone in any service layer. See here for [here](#how-to-apply-validators-to-a-bean).
Also can be used with Spring Boot, either through Bean Validation (JSR 303) via the `@ValidateBeanWith` annotation or as a native Spring validator via `VluentSpringValidator`. See [here](#spring-boot) for more details.



---

## Why Vluent

|                                 | if/else | Bean Validation | Vluent |
|---------------------------------|---------|-----------------|--------|
| Test a single rule in isolation | ❌       | ❌               | ✅      |
| Reuse a rule in two validators  | ❌       | ⚠️              | ✅      |
| Conditional cross-field rules   | ❌       | ❌               | ✅      |
| Compose conditions              | ❌       | ❌               | ✅      |
| Zero framework coupling         | ✅       | ❌               | ✅      |

---

## Installation

**Maven:**

```xml

<dependency>
  <groupId>io.github.drndivoje</groupId>
  <artifactId>vluent</artifactId>
  <version>0.0.4</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'io.github.drndivoje:vluent:0.0.4'
```

Zero transitive dependencies. Bean Validation (Jakarta) and Spring support are optional —
only add Hibernate Validator if you need `@ValidateBeanWith` integration, and Spring
(`spring-context`) if you use `VluentSpringValidator`. Spring Boot applications already have both.

---

## Quick start

Validators can be defined as lambdas or classes, and reused across multiple validation flows.

### 1. How to define validators:

Define validators as named constants using Lambdas

```java
static final Validator<Integer> LEGAL_AGE =
    age -> age >= 18
        ? ValidationResult.SUCCESS
        : ValidationResult.createError("Must be at least 18");

static final Validator<Double> MINIMUM_INCOME =
    income -> income >= 24_000
        ? ValidationResult.SUCCESS
        : ValidationResult.createError("Income below minimum threshold");
```

Or like a separate class:

```java
public class LegalAgeValidator implements Validator<Integer> {

  @Override
  public ValidationResult validate(Integer age) {
    return age >= 18
        ? ValidationResult.SUCCESS
        : ValidationResult.createError("Must be at least 18");
  }
}
```

### 2. Preconditions can be defined as lambdas

```java
Precondition isSelfEmployed = () -> "SELF_EMPLOYED".equals(user.employmentType());
Precondition isNewCustomer = () -> !user.isExistingCustomer();
```

**3.How to chain preconditions:**

Preconditions can be combined and validated

```java
ValidationResult result = Vluent.create()
    .on(user.getName(), NAME_PRESENT)
    .on(user.getIncome(), MINIMUM_INCOME)
    .when(isSelfEmployed.and(isNewCustomer))
    .then(user.getIncome(), SELF_EMPLOYED_INCOME)
    .validate();
```

where `SELF_EMPLOYED_INCOME`, `MINIMUM_INCOME`, and other validators are `Validator<Double>`
instances defined elsewhere.

### How to apply validators to a bean


If we have a validator class like this:
```java
public class LegalAgeValidator implements Validator<Integer> {

  @Override
  public ValidationResult validate(Integer age) {
    return age >= 18
        ? ValidationResult.SUCCESS
        : ValidationResult.createError("Must be at least 18");
  }
}
```

to Apply it to a bean, we can use the `@ValidateWith` annotation on the field:

```java
class User {
  @ValidateWith(LegalAgeValidator.class)
  private Integer age;
} 
```

You can also use `@ValidateBeanWith` on bean components if you want to be compatible with Bean
Validation API (JSR 303).
If you are using Hibernate Validator, it will work with other annotations from Hibernate Validator
as well.


import jakarta.validation.constraints.NotEmpty;

```java
@ValidateBeanWith(UserValidator.class)
class User {

  @NotNull private String name;
  @NotEmpty private double income;
  private String employmentType;
  private boolean existingCustomer;
}
```

In this case the @ValidateBeanWith annotation will work with @NotNull and @NotEmpty annotations as
well, and you can use them together to validate the bean.


## Spring Boot

Vluent integrates with Spring's `@Valid` in two ways. Both use the same Vluent `Validator<T>`,
so a rule written once works in Spring and standalone — no duplication, no separate
web-layer classes.

|                                     | JSR-303: `@ValidateBeanWith`                | Spring: `VluentSpringValidator`              |
|-------------------------------------|---------------------------------------------|----------------------------------------------|
| How it is wired                     | Annotation on the bean                      | Registered in an `@InitBinder` method        |
| Works without Spring                | ✅ (any Bean Validation provider)            | ❌                                            |
| Validator can be a Spring bean      | ❌ (created via no-arg constructor)          | ✅ (you create or inject it)                  |
| Error code of the reported error    | `ValidateBeanWith`                          | `Vluent`, or a custom code                   |
| Error arguments                     | Annotation attributes                       | None                                         |

In both cases a failure raises `MethodArgumentNotValidException` (HTTP 400), together with the
errors of standard annotations such as `@NotBlank`, and the message returned by the Vluent
validator becomes the error's default message.

> All classes used below are in the `io.github.drndivoje.vluent.api` package (since `0.0.5`).

### Option 1: Bean Validation (JSR 303) with `@ValidateBeanWith`

Annotate the bean with the Vluent validator(s) and use `@Valid` as usual:

```java
@ValidateBeanWith(AddressValidator.class)
public record Address(
    @NotBlank String street,
    @NotBlank String city,
    String zipCode,
    @NotBlank String country) {}

@PostMapping("/users/{name}/address")
public ResponseEntity<Address> addAddress(
    @PathVariable String name, @RequestBody @Valid Address address) {
  ...
}
```

The validator class needs a public no-arg constructor. It can use the full fluent API:

```java
public class AddressValidator implements Validator<Address> {

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
```

### Option 2: Spring validator with `VluentSpringValidator`

`VluentSpringValidator` adapts a Vluent `Validator<T>` to Spring's
`org.springframework.validation.Validator`. Register it for the request body in an
`@InitBinder` method. The bean itself needs no Vluent annotation:

```java
public record User(@NotBlank String name, LocalDate birthDate, double salary) {}

@RestController
@RequestMapping("/users")
public class UserController {

  @InitBinder("user")
  void initBinder(WebDataBinder binder) {
    binder.addValidators(new VluentSpringValidator<>(User.class, new UserValidator()));
  }

  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
    ...
  }
}
```

`addValidators` keeps Spring's default Bean Validation, so `@NotBlank` and the Vluent validator
both run. Failures are reported as an object error with the error code `Vluent`. Pass your own
code to use it as a key in `messages.properties`:

```java
new VluentSpringValidator<>(User.class, new UserValidator(), "user.invalid");
```

Because you create the adapter yourself, the Vluent validator can be a Spring bean with injected
dependencies (e.g. a repository to check that an email is unique).

### Example application

The [example](example) folder contains a Spring Boot application that uses both options:
`/users` uses `VluentSpringValidator` and `/users/{name}/address` uses `@ValidateBeanWith`.
See its [README](example/README.md) for how to run it and try it with `curl`.

---

## License

MIT — see [LICENSE.md](LICENSE.md)
