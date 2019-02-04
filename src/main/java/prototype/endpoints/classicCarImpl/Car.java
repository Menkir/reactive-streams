package prototype.endpoints.classicCarImpl;

import prototype.endpoints.ICar;
import prototype.model.Coordinate;
import prototype.routing.routeImpl.RectangleRoute;

import javax.inject.Inject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class Car implements ICar {
	private final InetSocketAddress socketAddress;
	private Socket clientSocket;
	private CompletableFuture<Void> clientThread;

	public int getFlowrate() {
		return flowrate;
	}

	public int flowrate = 0;

	@Inject
	public Car(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	@Override
	public void connect() {
		this.run();
	}

	public void close() throws IOException, InterruptedException {
		clientSocket.close();
	}

	private void run() {
		clientSocket = new Socket();
		try {
			clientSocket.connect(socketAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i = 0; i< 10_000; ++i){
			new RectangleRoute().getRouteAsStream().forEach(this::sendData);
		}

		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendData(Coordinate coordinate){
		try {
			// SEND
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(coordinate);
			oos.flush();

			// RECEIVE
			ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
			Coordinate c = (Coordinate) ois.readObject();

			// INCREMENT flowrate for analysis
			++flowrate;

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
