package prototype.app;

import java.time.Duration;
import java.util.Scanner;
import prototype.client.Client;
import prototype.client.ClientConfiguration;
import prototype.server.Server;
import prototype.view.Monitor;


import static prototype.routing.RoutingFactory.RouteType.RECTANGLE;
import static prototype.routing.RoutingFactory.RouteType.TRIANGLE;

class App {

    public static void main(final String... args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        Server server = new Server();
        new Monitor(server);

        new Client(new ClientConfiguration(Duration.ofMillis(50), RECTANGLE));
        Thread.sleep(100);
        //new Client(new ClientConfiguration(Duration.ofMillis(100), TRIANGLE));

        System.out.println("Press CTRL+D to terminate Application");
        while (scanner.hasNext()) {

        }
        server.dispose();
    }
}
