package prototype.app;

import java.time.Duration;
import java.util.Scanner;
import prototype.client.Client;
import prototype.client.ClientConfiguration;
import prototype.server.Server;
import prototype.view.Monitor;


import static routing.RoutingFactory.RouteType.RECTANGLE;

class App {

    public static void main(final String... args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        Server server = new Server();
        new Monitor(server);

        for(int i = 0; i < 100; ++i){
            new Client(new ClientConfiguration(Duration.ofMillis(10), RECTANGLE, 1));
            Thread.sleep(50);
        }

        System.out.println("Press CTRL+D to terminate Application");
        while (scanner.hasNext()) {

        }
        server.dispose();
    }
}
