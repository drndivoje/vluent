package com.drnd.vluent.model;

import java.util.function.Supplier;

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