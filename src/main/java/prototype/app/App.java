package prototype.app;

import java.time.Duration;
import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import prototype.endpoints.carImpl.Car;
import prototype.endpoints.carImpl.CarConfiguration;
import prototype.endpoints.serverImpl.Server;
import prototype.view.Monitor;
import reactor.core.publisher.Flux;


import static prototype.routing.RoutingFactory.RouteType.CIRCLE;
import static prototype.routing.RoutingFactory.RouteType.RECTANGLE;
import static prototype.routing.RoutingFactory.RouteType.TRIANGLE;

class App {
    public static void main(final String... args) throws InterruptedException {
        final int DELAY = 1; // second
        Scanner scanner = new Scanner(System.in);

        Injector injector = Guice.createInjector(new EndpointsModule());

        Server server = injector.getInstance(Server.class);
        server.receive();
        Car car = injector.getInstance(Car.class);
        car.connect();
        car.requestChannel();
        car.subscribeOnServerEndpoint();


        System.out.println("Press CTRL+D to terminate Application");
        while (scanner.hasNext()) {

        }
        /*server.dispose();
        car.dispose();*/
    }
}
