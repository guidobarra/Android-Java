package com.gubadev.soaapp.client;

import com.gubadev.soaapp.dto.Event;
import com.gubadev.soaapp.dto.ResponseGeneric;
import com.gubadev.soaapp.dto.UserCredential;
import com.gubadev.soaapp.dto.UserDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface APICatedraSOA {

    @POST("register")
    Call<ResponseGeneric> registerUser(@HeaderMap Map<String, String> headers, @Body UserDTO user);

    @POST("login")
    Call<ResponseGeneric> logIn(@HeaderMap Map<String, String> headers, @Body UserCredential user);

    @PUT("refresh")
    Call<ResponseGeneric> updateToken(@HeaderMap Map<String, String> headers);

    @POST("event")
    Call<ResponseGeneric> saveEvent(@HeaderMap Map<String, String> headers, @Body Event event);
}
