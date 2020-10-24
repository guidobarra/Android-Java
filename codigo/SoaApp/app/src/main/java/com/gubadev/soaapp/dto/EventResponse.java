package com.gubadev.soaapp.dto;

import com.google.gson.annotations.SerializedName;

public class EventResponse {

    @SerializedName("type_events")
    private String typeEvent;

    @SerializedName("dni")
    private Long dni;

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private Long id;

    public EventResponse(String typeEvent, Long dni, String description, Long id) {
        this.typeEvent = typeEvent;
        this.dni = dni;
        this.description = description;
        this.id = id;
    }
}
