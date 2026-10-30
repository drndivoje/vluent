# Vluent Spring Boot example

A small Spring Boot application that shows the two ways to use [Vluent](../README.md) with
Spring's `@Valid`:

| Endpoint                          | Integration                                    | Bean      | Vluent validator   |
|-----------------------------------|------------------------------------------------|-----------|--------------------|
| `POST /users`                     | Spring validator (`VluentSpringValidator`)     | `User`    | `UserValidator`    |
| `POST /users/{name}/address`      | Bean Validation / JSR 303 (`@ValidateBeanWith`) | `Address` | `AddressValidator` |
| `GET /users`                      | –                                              | –         | –                  |

Users and addresses are kept in memory, so they are lost when the application restarts.

## The two examples

### Spring validator: users

`UserValidator` checks that the user is at least 18 years old. `User` has no Vluent annotation.
Instead, `UserController` registers the validator for the `user` request body:

```java
@InitBinder("user")
void initBinder(WebDataBinder binder) {
  binder.addValidators(new VluentSpringValidator<>(User.class, new UserValidator()));
}
```

The `@NotBlank` on `User.name` is still checked by Spring's default Bean Validation.

### JSR 303: addresses

`Address` is annotated with `@ValidateBeanWith(AddressValidator.class)`, so `@Valid` in
`AddressController` is all that is needed. `AddressValidator` uses the fluent API with a
precondition:

- the zip code is required;
- if the country is `DE`, the zip code must have 5 digits.

`street`, `city` and `country` are additionally checked with `@NotBlank`.

## Running the application

The example depends on the current snapshot of Vluent, so install the library first. From the
repository root:

```bash
./mvnw install
```

Then start the example:

```bash
cd example
./mvnw spring-boot:run
```

The application listens on `http://localhost:8080`.

## Trying it with curl

The examples below use `-i` to show the HTTP status. Validation errors return `400 Bad Request`,
and the details are logged by the application as a `MethodArgumentNotValidException` warning.

### Users (Spring validator)

Create a valid user: **201 Created**

```bash
curl -i -X POST localhost:8080/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ivan","birth_date":"1990-01-01","salary":50000}'
```

User younger than 18: **400 Bad Request**

```bash
curl -i -X POST localhost:8080/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"Kid","birth_date":"2020-01-01","salary":0}'
```

Logged error:

```
[Error in object 'user': codes [Vluent.user,Vluent]; arguments []; default message [to young]]
```

Blank name and younger than 18: **400 Bad Request** with 2 errors, one from `@NotBlank` and one
from `UserValidator`

```bash
curl -i -X POST localhost:8080/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"","birth_date":"2020-01-01","salary":0}'
```

List all users: **200 OK**

```bash
curl -i localhost:8080/users
```

### Addresses (JSR 303)

The user must exist, so create `Ivan` first (see above).

Valid German address: **201 Created**

```bash
curl -i -X POST localhost:8080/users/Ivan/address \
  -H 'Content-Type: application/json' \
  -d '{"street":"Main 1","city":"Berlin","zip_code":"10115","country":"DE"}'
```

German address with an invalid zip code: **400 Bad Request**

```bash
curl -i -X POST localhost:8080/users/Ivan/address \
  -H 'Content-Type: application/json' \
  -d '{"street":"Main 1","city":"Berlin","zip_code":"123","country":"DE"}'
```

Logged error:

```
[Error in object 'address': codes [ValidateBeanWith.address,ValidateBeanWith]; ...; default message [German zip code must have 5 digits]]
```

Short zip code outside Germany, so the German rule does not apply: **201 Created**

```bash
curl -i -X POST localhost:8080/users/Ivan/address \
  -H 'Content-Type: application/json' \
  -d '{"street":"Knez 1","city":"Beograd","zip_code":"110","country":"RS"}'
```

Missing zip code and blank city: **400 Bad Request** with 2 errors, one from `AddressValidator`
and one from `@NotBlank`

```bash
curl -i -X POST localhost:8080/users/Ivan/address \
  -H 'Content-Type: application/json' \
  -d '{"street":"Main 1","city":"","country":"RS"}'
```

Address for a user that does not exist: **404 Not Found**

```bash
curl -i -X POST localhost:8080/users/Nobody/address \
  -H 'Content-Type: application/json' \
  -d '{"street":"Main 1","city":"Berlin","zip_code":"10115","country":"DE"}'
```
