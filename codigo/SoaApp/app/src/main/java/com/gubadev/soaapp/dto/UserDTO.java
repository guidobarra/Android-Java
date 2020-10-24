package com.gubadev.soaapp.dto;

import com.google.gson.annotations.SerializedName;

public class UserDTO {

    @SerializedName("env")
    private String env;

    @SerializedName("name")
    private String firstName;

    @SerializedName("lastname")
    private String lastName;

    @SerializedName("dni")
    private Long dni;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("commission")
    private Long commission;

    public UserDTO() {

    }

    public UserDTO(String env, String firstName, String lastName, Long dni, String email, String password, Long commission) {
        this.env = env;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.commission = commission;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCommission(Long commission) {
        this.commission = commission;
    }

    public String getEnv() {
        return env;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Long getCommission() {
        return commission;
    }
}
