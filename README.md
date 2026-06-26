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

You can not  test these rules individually. You can not reuse them elsewhere.
Adding a new rule means finding the right place and hoping ordering still holds.

## The solution

```java
return Vluent.create()
    .on(app.name(),          NAME_PRESENT)
    .on(app.age(),           LEGAL_AGE)
    .when(isSelfEmployed)
    .then(app,               SELF_EMPLOYED_INCOME)
    .when(isLargeLoan.and(isExistingCustomer.not()))
    .then(app.creditScore(), EXCELLENT_CREDIT)
    .validate();
```

Reads like a policy document. Every rule is a named object.
Every condition composes with `.and()`, `.or()`, `.not()`.
Every validator tests in one line — no full object construction, no ordering concerns.

```java
assertThat(LEGAL_AGE.validate(17).isSuccess()).isFalse();
assertThat(SELF_EMPLOYED_INCOME.validate(app).isSuccess()).isFalse();
```


---

## Why Vluent

| | if/else | Bean Validation | Vluent |
|---|---|---|---|
| Test a single rule in isolation | ❌ | ❌ | ✅ |
| Reuse a rule in two validators | ❌ | ⚠️ | ✅ |
| Conditional cross-field rules | ❌ | ❌ | ✅ |
| Compose conditions | ❌ | ❌ | ✅ |
| Zero framework coupling | ✅ | ❌ | ✅ |

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

Zero transitive dependencies. Bean Validation (Jakarta) support is optional —
only add Hibernate Validator if you need `@ValidateBeanWith` integration.

---

## Quick start

**1. Define validators as named constants:**

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

**2. Define preconditions as lambdas:**

```java
Precondition isSelfEmployed = () -> "SELF_EMPLOYED".equals(user.employmentType());
Precondition isNewCustomer  = () -> !user.isExistingCustomer();
```

**3. Compose and validate:**

```java
ValidationResult result = Vluent.create()
    .on(user.getName(),     NAME_PRESENT)
    .on(user.getIncome(),   MINIMUM_INCOME)
    .when(isSelfEmployed.and(isNewCustomer))
    .then(user.getIncome(), SELF_EMPLOYED_INCOME)
    .validate();
```

---

## Spring Boot

Works with `@Valid` out of the box via `@ValidateBeanWith`:

```java
@ValidateBeanWith(UserValidator.class)
public record User(@NotBlank String name, LocalDate birthDate, double salary) {}

@PostMapping("/users")
public ResponseEntity<User> create(@Valid @RequestBody User user) {
  return ResponseEntity.ok(userService.save(user));
}
```

The same validator works both with Bean Validation and standalone —
no duplication, no separate web-layer classes.

---

## License

MIT — see [LICENSE.md](LICENSE.md)
