package com.gubadev.soaapp.dto;

import com.google.gson.annotations.SerializedName;

public class ResponseGeneric {

    @SerializedName("success")
    private Boolean success;

    @SerializedName("env")
    private String env;

    @SerializedName("token")
    private String token;

    @SerializedName("token_refresh")
    private String tokenRefresh;

    @SerializedName("event")
    private EventResponse event;

    @SerializedName("msg")
    private String msg;

    public ResponseGeneric() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getEnv() {
        return env;
    }

    public String getToken() {
        return token;
    }

    public String getTokenRefresh() {
        return tokenRefresh;
    }

    public EventResponse getEvent() {
        return event;
    }

    public String getMsg() {
        return msg;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenRefresh(String tokenRefresh) {
        this.tokenRefresh = tokenRefresh;
    }

    public void setEvent(EventResponse event) {
        this.event = event;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
