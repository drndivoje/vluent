# TODO — Top 5 features

These are ordered by impact on adoption, mainly in Spring Boot applications. Each one starts from a
gap in the current code.

---

## 1. Rich error model: collect all errors, with field paths and codes

**Today:** `Vluent.validate()` stops at the first failure (`findFirst()`). `ValidationResult` is
just a `List<String>`. `VluentJakartaBeanValidator.isValid()` returns a plain `boolean`, so Spring
only reports the generic `"Vluent Validation failed"` and the real messages are lost.

**Goal:**
- Fail-fast and collect-all modes: `validate()` and `validateAll()` (or `Vluent.create(Mode.COLLECT_ALL)`).
- A structured `Violation`: `path` (e.g. `"address.zip"`), `code` (e.g. `"user.age.min"`),
  `message`, `rejectedValue`, `params`.
- `ValidationResult` becomes immutable. `SUCCESS` is currently a mutable, non-final public static
  that exposes a mutable list.
- The Bean Validation bridge sends each Vluent violation to `ConstraintValidatorContext`
  (`buildConstraintViolationWithTemplate(...).addPropertyNode(path)`). That way `@Valid` and
  `MethodArgumentNotValidException` show the real field errors.

```java
ValidationResult r = Vluent.create()
    .on("age", user.age(), LEGAL_AGE)
    .validateAll();
r.violations(); // [Violation{path=age, code=user.age.min, message=Must be at least 18}]
```

---

## 2. Spring Boot starter (`vluent-spring-boot-starter`)

**Today:** validators are created by reflection through a no-arg constructor (`Util`,
`AnnotationResolution`). This means a validator cannot inject a repository or service (for example
"email must be unique"). Every user has to wire the error handling themselves.

**Goal:**
- Auto-configuration that resolves `@ValidateBeanWith` / `@ValidateWith` validators as **Spring
  beans** when they exist, and falls back to reflection otherwise.
- A default `@RestControllerAdvice` that maps Vluent failures to RFC 9457 `ProblemDetail`
  (`400` with an `errors[]` list of path, code and message). It can be turned off with
  `vluent.web.problem-details.enabled=false`.
- `VluentException` plus `validateOrThrow()` for use in the service layer.
- Messages resolved through Spring's `MessageSource`, using the violation `code` as the key (i18n).
- Spring Boot tests in the `example` app that cover the whole flow.

```java
@Component
class UniqueEmail implements Validator<String> {
  private final UserRepository repo;
  UniqueEmail(UserRepository repo) { this.repo = repo; }
  public ValidationResult validate(String email) { ... }
}
```

---

## 3. Type-safe, reusable validator definitions

**Today:** a `Vluent` chain is built for each *instance*: values are captured eagerly in `on(...)`,
and `when(...).then(...)` evaluates the precondition while the chain is being built. Rules cannot be
declared once for a type and reused. `on(T)` only reads `getDeclaredFields()`, so inherited fields
and nested objects are ignored.

**Goal:** a declarative builder that produces a reusable `Validator<T>`. The result plugs straight
into `@ValidateBeanWith` or `Vluent.on(...)`:

```java
static final Validator<LoanApplication> LOAN_RULES = Vluent.forType(LoanApplication.class)
    .field("name", LoanApplication::name, NOT_BLANK)
    .field("age", LoanApplication::age, LEGAL_AGE)
    .when(app -> app.isSelfEmployed())
        .then("annualIncome", LoanApplication::annualIncome, min(40_000))
    .nested("address", LoanApplication::address, ADDRESS_RULES)
    .each("items", LoanApplication::items, ITEM_RULES)   // paths: items[0].price
    .build();
```

- Preconditions become `Predicate<T>` and are evaluated lazily, when validation runs.
- Field names are used to build the violation paths from feature #1.
- Support for nested objects and collections, records, and inherited fields.

---

## 4. Built-in validators and validator combinators

**Today:** users must write even trivial rules themselves (not null, min, max, pattern). `Validator`
only has `invert()`, and its error message cannot be configured.

**Goal:**
- A `Validators` class of ready-made rules: `notNull`, `notBlank`, `min`, `max`, `between`,
  `length`, `pattern`, `email`, `oneOf`, `past`, `future`, `minAge`, `notEmpty`, `size`.
- Combinators on `Validator<T>`: `and`, `or`, `withMessage(...)`, `withCode(...)`,
  `nullSafe()` / `optional()`, and `map(Function<U,T>)` to validate a derived value.
- Null-safety by default. Today the README `LEGAL_AGE` example throws an NPE on a `null` age.

```java
Validator<String> USERNAME = notBlank().and(length(3, 20)).and(pattern("[a-z0-9_]+"))
    .withCode("user.username.invalid");
```

---

## 5. Async / reactive validation

**Today:** everything is synchronous. Rules that call a database, a remote API or a cache block the
thread and cannot run in parallel. That fits poorly with WebFlux and virtual-thread or reactive
services.

**Goal:**
- `AsyncValidator<T>` that returns `CompletionStage<ValidationResult>`, plus
  `validateAsync()` on the chain. Independent steps run concurrently, with an optional
  `Executor` (virtual threads by default on Java 21).
- An optional `vluent-reactor` module with `Mono<ValidationResult>` for Spring WebFlux.
- Timeouts per step and short-circuiting in fail-fast mode.
