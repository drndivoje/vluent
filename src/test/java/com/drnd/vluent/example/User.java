package com.drnd.vluent.example;

import java.time.LocalDate;

/**
 * author: drndivoje
 */
public class User {
    private final String name;
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
