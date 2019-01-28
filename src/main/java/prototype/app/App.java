package prototype.app;

import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import prototype.endpoints.carImpl.Car;
import prototype.endpoints.serverImpl.Server;
import prototype.view.Monitor;
import reactor.core.Disposable;
import rx.Subscription;

class App {
    public static void main(final String... args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        Injector injector = Guice.createInjector(new ReactiveModule());

        Server server = injector.getInstance(Server.class);
        server.receive();

        Monitor monitor = new Monitor(server);
        monitor.start();
        Disposable carsEndpoint = monitor.listeningOnIncomingCars();
        Subscription coordinateSubscription = monitor.listeningOnIncomingCoordinates();

        Car car = injector.getInstance(Car.class);
        car.connect();
        car.requestChannel();
        Disposable serverEndpoint = car.subscribeOnServerEndpoint();


        //logger.log(Level.INFO, "Type STRG+D to terminate");
        while (scanner.hasNext()) {

        }
        carsEndpoint.dispose();
        coordinateSubscription.unsubscribe();
        server.dispose();
        serverEndpoint.dispose();
    }
}
