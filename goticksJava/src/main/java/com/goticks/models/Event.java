package com.goticks.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Event implements Serializable {

    @JsonProperty("event")
    private String eventName;
    @JsonProperty("tickets")
    private List<Ticket> tickets;

    @JsonCreator
    public Event(@JsonProperty(value = "event") String eventName,@JsonProperty(value = "tickets") List<Ticket> tickets) {
        this.eventName = eventName;
        this.tickets = tickets;
    }

    @JsonProperty("tickets")
    public List<Ticket> gettickets() {
        return tickets;
    }

    @JsonProperty("tickets")
    public void settickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @JsonProperty("event")
    public String getEventName() {
        return eventName;
    }

    @JsonProperty("event")
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
