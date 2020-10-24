package com.gubadev.soaapp.dto;

public class User {

    private Long id;

    private String firstName;

    private String lastName;

    private Long dni;

    private String email;

    private String password;

    public User(String firstName, String lastName, Long dni, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.email = email;
        this.password = password;
    }
}
