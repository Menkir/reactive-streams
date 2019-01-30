package prototype.app;

import prototype.endpoints.classicCarImpl.Car;
import prototype.endpoints.classicServerImpl.Server;
import prototype.view.ClassicMonitor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import static prototype.routing.RoutingFactory.RouteType.RECTANGLE;

public class ClassicApp {
	public static void main(final String... args) throws IOException, InterruptedException {
		Scanner sc = new Scanner(System.in);
		InetSocketAddress socketAddress = new InetSocketAddress("10.168.10.155", 1337);

		Server server = new Server(socketAddress);
		server.receive();
		ClassicMonitor monitor = new ClassicMonitor(server);
		monitor.start();
		Car[] cars = new Car[5];
		for(int i = 0; i< cars.length; ++i){
			cars[i] = new Car(socketAddress);
			cars[i].connect();
			//
		}

		while(sc.hasNext()){}

		for (Car car : cars) {
			car.close();
		}
		System.out.println("Cars closed");
		server.close();
		System.out.println("Server closed");

	}
}
