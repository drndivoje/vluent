/**
 * Vluent - composable, testable Java validation rules.
 *
 * <p>Only {@code io.github.drndivoje.vluent.api} is public API. Everything under {@code
 * io.github.drndivoje.vluent.internal} is an implementation detail and may change without notice.
 *
 * <p>Jakarta Bean Validation is an optional dependency. It is only needed when {@link
 * io.github.drndivoje.vluent.api.ValidateBeanWith} is used.
 *
 * <p>{@link io.github.drndivoje.vluent.api.Vluent#on(Object)} reads annotated fields reflectively,
 * so modules that use it must open the packages of the validated classes to this module.
 */
module io.github.drndivoje.vluent {
  requires static jakarta.validation;

  exports io.github.drndivoje.vluent.api;

  // Not exported (clients cannot compile against it), but opened so that a Bean Validation
  // provider (Hibernate Validator, Spring's ConstraintValidatorFactory, ...) can instantiate
  // VluentJakartaBeanValidator reflectively at runtime.
  opens io.github.drndivoje.vluent.internal.beanvalidation;
}
