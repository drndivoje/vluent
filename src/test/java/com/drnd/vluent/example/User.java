package com.drnd.vluent.example;

import com.drnd.vluent.model.ValidateWith;

import java.time.LocalDate;

/**
 * author: drndivoje
 */
public class User {
    private final String name;
    @ValidateWith(value = OlderThen18Validator.class)
    private final LocalDate birthday;
    private final double salary;

    public User(String name, LocalDate birthday, double salary) {
        this.name = name;
        this.birthday = birthday;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public double getSalary() {
        return salary;
    }
}
