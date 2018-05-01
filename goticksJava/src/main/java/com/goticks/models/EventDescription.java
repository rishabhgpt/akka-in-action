package com.goticks.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)

public class EventDescription implements Serializable {
    @JsonProperty("tickets")
    private int tickets;

    @JsonCreator
    EventDescription(@JsonProperty(value = "tickets") int tickets) {
        super();
        this.tickets = tickets;
    }

    @JsonProperty("tickets")
    public int getTickets() {
        return tickets;
    }

    @JsonProperty("tickets")
    public void setTickets(int tickets) {
        this.tickets = tickets;
    }
}
