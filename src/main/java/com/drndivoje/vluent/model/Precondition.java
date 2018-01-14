package com.drndivoje.vluent.model;

import java.util.function.Supplier;

/**
 * Used to make precondition before executing validation.
 * It is added to validation chain using {@link com.drndivoje.vluent.Vluent} when method
 */
public interface Precondition extends Supplier<Boolean> {
    /**
     * It combines preconditions with logical and
     *
     * @param other other precondition
     * @return combine precondition with AND logical operator
     */
    default Precondition and(Precondition other) {
        return () -> this.get() && other.get();
    }

    /**
     * It combines preconditions with logical or
     *
     * @param other other precondition
     * @return combine precondition with OR logical operator
     */
    default Precondition or(Precondition other) {
        return () -> this.get() || other.get();
    }

    /**
     * It negates a precondition
     *
     * @return negated precondition
     */
    default Precondition not() {
        return () -> !this.get();
    }

}