package io.github.drndivoje.vluent.internal;

import io.github.drndivoje.vluent.api.Validator;
import io.github.drndivoje.vluent.api.Vluent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * It holds all validation steps (as {@link Step}) configured calling API callis via {@link Vluent}
 * class.
 */
public class Chain implements Iterable<Step<?>> {

  private final LinkedList<Step<?>> chain;

  public Chain() {
    this.chain = new LinkedList<>();
  }

  public <T> void add(T value, Validator<T> validator) {
    chain.add(new Step<>(validator, () -> value));
  }

  public <T> void addAll(T value, List<Validator<T>> validators) {
    for (Validator<T> validator : validators) {
      this.add(value, validator);
    }
  }

  public Stream<Step<?>> stream() {
    return chain.stream();
  }

  @Override
  public Iterator<Step<?>> iterator() {
    return chain.iterator();
  }

  public <T> void add(Supplier<T> valueSupplier, Validator<T> validator) {
    chain.add(new Step<>(validator, valueSupplier));
  }
}
