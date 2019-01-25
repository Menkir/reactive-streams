package prototype.app;

import java.time.Duration;
import java.util.Scanner;
import prototype.client.Client;
import prototype.client.ClientConfiguration;
import prototype.server.Server;
import prototype.view.Monitor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


import static prototype.routing.RoutingFactory.RouteType.CIRCLE;
import static prototype.routing.RoutingFactory.RouteType.RECTANGLE;
import static prototype.routing.RoutingFactory.RouteType.TRIANGLE;

class App {
    public static void main(final String... args) throws InterruptedException {
        final int DELAY = 1; // second
        Scanner scanner = new Scanner(System.in);
        Server server = new Server();
        new Monitor(server);
        Flux.just(
            new Client(new ClientConfiguration(Duration.ofMillis(DELAY*300), CIRCLE)),
            new Client(new ClientConfiguration(Duration.ofMillis(DELAY*500), TRIANGLE)),
            new Client(new ClientConfiguration(Duration.ofMillis(DELAY*100), RECTANGLE))
        ).delaySequence(Duration.ofMillis(100)).subscribe();

        System.out.println("Press CTRL+D to terminate Application");
        while (scanner.hasNext()) {

        }
        server.dispose();
    }
}
