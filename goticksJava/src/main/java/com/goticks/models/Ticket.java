package com.goticks.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Ticket implements Serializable{

    @JsonProperty("ticket_id")
    private int ticketId;

    public Ticket(int id) {
        ticketId = id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }
}
