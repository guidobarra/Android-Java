package com.gubadev.soaapp.dto;

import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("env")
    private String env;

    @SerializedName("type_events")
    private String typeEvent;

    @SerializedName("description")
    private String description;

    public Event(String env, String typeEvent, String description) {
        this.env = env;
        this.typeEvent = typeEvent;
        this.description = description;
    }
}
