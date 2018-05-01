package com.goticks.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.util.OptionVal;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.goticks.models.Event;
import com.goticks.models.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.Option;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoxOffice extends AbstractActor {

    private List<Event> events;
    private Logger logger = LogManager.getLogger(BoxOffice.class);
    public static Props props(List<Event> events) {
        return Props.create(BoxOffice.class, events);
    }

    private BoxOffice(List<Event> events) {
        this.events = events;
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AddEvent.class, event -> {
                    Optional<ActorRef> childActorOptional = getContext().findChild(event.event);
                    List<Ticket> ticketSellerTickets= IntStream.rangeClosed(1,event.tickets).boxed()
                            .map(Ticket::new)
                            .collect(Collectors.toList());
                    if (childActorOptional.isPresent()) {
                        logger.warn("ticket seller already exists");
                        sender().tell(new EventExists(),self());
                    }
                    else {
                        logger.info("creating new ticket seller actor");
                        context().actorOf(TicketSeller.props(event.event),event.event).tell(new TicketSeller.AddTickets(ticketSellerTickets),self());
                        sender().tell(new EventCreated(new Event(event.event,ticketSellerTickets)),self());
                    }
                }
                )
                .match(GetEvent.class, (GetEvent event) -> {
                    Option<ActorRef> childActorRef = context().child(event.eventName);
                    Option<Void> map = childActorRef.map((ActorRef child) -> {
                        return child.forward(new TicketSeller.GetEvent(event.eventName), getContext());
                    });
                })
                .matchAny(msg->{
                    System.out.println("received unknown message");
                }).build();
    }

    public static class AddEvent {
        String event;
        int tickets;
        public AddEvent(String event, int tickets) {
            this.event = event;
            this.tickets = tickets;
        }
    }

    public interface EventResponse { }
    public static class EventCreated implements Serializable, EventResponse{
        @JsonProperty("event")
        Event event;

        EventCreated(Event event) {
            this.event = event;
        }

        @JsonGetter("event")
        public Event getEvent() {
            return event;
        }
    }
    public static class EventExists implements Serializable, EventResponse {
    }


    public static class Events implements Serializable{

        @JsonProperty("events")
        List<Event> events;

        @JsonCreator
        Events(List<Event> events) {
            this.events = events;
        }

        @JsonGetter(value = "events")
        List<Event> getEvents() {
            return events;
        }
    }

    public static class GetEvents {}
    public static class GetEvent {
        String eventName;
        public GetEvent(String eventName) {
            this.eventName = eventName;
        }
    }
}
