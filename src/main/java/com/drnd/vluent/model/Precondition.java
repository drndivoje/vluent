package com.drnd.vluent.model;

import java.util.function.Supplier;

/**
 * Used to make precondition before executing validation.
 * It is added to validation chain using Vluent.when method
 */
public interface Precondition extends Supplier<Boolean> {
    default Precondition and(Precondition other) {
        return () -> this.get() && other.get();
    }

    default Precondition or(Precondition other) {
        return () -> this.get() || other.get();
    }

    default Precondition not() {
        return () -> !this.get();
    }

}