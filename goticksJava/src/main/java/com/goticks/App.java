package com.goticks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;


public class App extends AllDirectives {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        final Router router = new Router();
        final Config akkaConfig = ConfigFactory.load("app");
        final ActorSystem system = ActorSystem.create("app",akkaConfig);
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Flow<HttpRequest,HttpResponse, NotUsed> routeFlow = router.createRoute(system).flow(system,materializer);
        http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost",8081),
                materializer);
        System.out.println("Server listening on port 8081\nPress Enter to exit");
        System.in.read();
    }
}
