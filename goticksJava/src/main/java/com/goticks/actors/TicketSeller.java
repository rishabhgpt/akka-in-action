package com.goticks.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.goticks.models.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketSeller extends AbstractActor {

    private String eventName;
    private List<Ticket> tickets = new ArrayList<>();
    public static Props props(String eventName) {
        return Props.create(TicketSeller.class,eventName);
    }
    private TicketSeller(String eventName) {
        this.eventName = eventName;
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AddTickets.class,addTickets -> {
                    tickets.addAll(addTickets.tickets);
                })
                .match(GetEvent.class,event -> {

                })
                .matchAny(msg -> {
                    System.out.println("unknown msg received in ticket seller "+msg);
                }).build();
    }

    public static class AddTickets{
        List<Ticket> tickets;
        AddTickets(List<Ticket> tickets) {
            this.tickets = tickets;
        }
    }

    public static class GetEvent {
        String eventName;
        public GetEvent(String eventName) {
            this.eventName = eventName;
        }
    }
}
