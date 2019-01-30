package prototype.endpoints.classicCarImpl;

import prototype.endpoints.ICar;
import prototype.model.Coordinate;
import prototype.routing.routeImpl.RectangleRoute;
import prototype.utility.Serializer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Car implements ICar {
	private final InetSocketAddress socketAddress;
	private Socket clientSocket;
	private CompletableFuture<Void> clientThread;
	public Car(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	@Override
	public void connect() {
		//System.out.println("[CLIENT] " + hashCode() + " start");
		this.run();
	}

	public void close() throws IOException, InterruptedException {
		clientSocket.close();
	}

	private void run() {
		clientSocket = new Socket();
		//System.out.println("[CLIENT] " + hashCode() + " new Socket instaniated");
		try {
			//System.out.println("[CLIENT] " + hashCode() + " try connection");
			clientSocket.connect(socketAddress);
			//System.out.println("[CLIENT] " + hashCode() + " succssfully connected");
		} catch (IOException e) {
			e.printStackTrace();
		}


		RectangleRoute rectangleRoute = new RectangleRoute();
		rectangleRoute.getRouteAsStream().forEach(this::sendData);


		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendData(Coordinate coordinate){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(coordinate);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String s = Arrays.toString(Serializer.serialize(new Coordinate(0, 0)));
	}

	private static void delay() throws InterruptedException {
		Thread.sleep(200);
	}
}
