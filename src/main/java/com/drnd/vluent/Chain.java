package com.drnd.vluent;

import com.drnd.vluent.model.Validator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

class Chain implements Iterable<Step> {
    private final LinkedList<Step> chain;

    Chain() {
        this.chain = new LinkedList<>();
    }

    <T> void add(T value, Validator<T> validator) {
        chain.add(new Step<>(validator, () -> value));
    }

    <T> void addAll(T value, List<Validator<T>> validators) {
        for(Validator<T> validator : validators) {
            this.add(value, validator);
        }
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
