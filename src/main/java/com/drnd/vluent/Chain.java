package com.drnd.vluent;

import com.drnd.vluent.model.Validator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;

class Chain implements Iterable<Step> {
    private final LinkedList<Step> chain;

    Chain() {
        this.chain = new LinkedList<>();
    }

    public <T> void add(T value, Validator<T> validator) {
        chain.add(new Step<>(validator, () -> value));
    }

    @Override
    public Iterator<Step> iterator() {
        return chain.iterator();
    }

    public <T> void add(Supplier<T> valueSupplier, Validator<T> validator) {
        chain.add(new Step<>(validator, valueSupplier));
    }

    public void add(Step last) {
        chain.add(last);
    }
}
