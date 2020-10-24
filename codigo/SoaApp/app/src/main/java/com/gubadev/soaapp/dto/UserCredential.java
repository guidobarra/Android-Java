package com.gubadev.soaapp.dto;

import com.google.gson.annotations.SerializedName;

public class UserCredential {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public UserCredential(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
