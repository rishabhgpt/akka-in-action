package com.goticks;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import akka.http.scaladsl.model.StatusCodes;
import akka.util.Timeout;
import com.goticks.actors.BoxOffice;
import com.goticks.models.Event;
import com.goticks.models.EventDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.concurrent.duration.FiniteDuration;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;

public class Router extends AllDirectives {
    private static Logger logger = LogManager.getLogger();
    final Timeout timeout = Timeout.durationToTimeout(FiniteDuration.apply(5, TimeUnit.SECONDS));

    protected Route createRoute(ActorSystem system) {
        ActorRef boxOfficeActor = system.actorOf(BoxOffice.props(new ArrayList<>()),"boxOfficeActor");
        return route(path("_status",()->get(()->complete(StatusCodes.OK()))),
                post(() -> pathPrefix(PathMatchers.segment("event").slash(),() -> entity(Jackson.unmarshaller(EventDescription.class),
                        eventDescription -> extractUnmatchedPath(
                                eventName -> {
                                    CompletionStage<BoxOffice.EventResponse> eventResponseCompletionStage = ask(boxOfficeActor, new BoxOffice.AddEvent(eventName, eventDescription.getTickets()), timeout)
                                            .thenApply(BoxOffice.EventResponse.class::cast);
                                    return onSuccess(() -> eventResponseCompletionStage,
                                            eventResponse ->
                                                    eventResponse instanceof BoxOffice.EventCreated?completeOK(eventResponse,Jackson.marshaller())
                                                            :complete(HttpResponse.create().withStatus(StatusCodes.BadRequest()).withEntity("Event already exists")));
                                }
                        ))
                )),
                get(() -> pathPrefix(PathMatchers.segment("events"), () -> route(pathEndOrSingleSlash(
                        () -> {
                            CompletionStage<BoxOffice.Events> eventResponseCompletionStage = ask(boxOfficeActor, new BoxOffice.GetEvents(), timeout)
                                    .thenApply(BoxOffice.Events.class::cast);
                            return onSuccess(() -> eventResponseCompletionStage,
                                    eventResponse -> completeOK(eventResponse, Jackson.marshaller()));
                        }
                )))),
                get(() -> pathPrefix(PathMatchers.segment("event").slash(), () -> extractUnmatchedPath(
                        eventName -> {
                            CompletionStage<Event> eventResponseCompletionStage = ask(boxOfficeActor, new BoxOffice.GetEvent(eventName), timeout)
                                    .thenApply(Event.class::cast);
                            return onSuccess(() -> eventResponseCompletionStage,
                                    eventResponse -> completeOK(eventResponse, Jackson.marshaller()));
                        }
                        )
                ))
                );
    }
}
