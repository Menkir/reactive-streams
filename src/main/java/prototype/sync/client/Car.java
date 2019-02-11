package prototype.sync.client;

import prototype.async.client.CarConfiguration;
import prototype.interfaces.ICar;
import prototype.model.Coordinate;
import prototype.routing.RoutingFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Car implements ICar {
	private final InetSocketAddress socketAddress;
	private Socket clientSocket;
	private CarConfiguration carConfiguration;
    private final int MAXELEMENTS = 100_000_000;
    private RoutingFactory routingFactory = new RoutingFactory();
    private List<Coordinate> route;
    private int flowrate;
    private boolean done = false;


	public Car(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
		carConfiguration = new CarConfiguration();
        this.route = routingFactory.getRoutingType(carConfiguration.ROUTETYPE).getRouteAsList();
        this.flowrate = 0;
	}

    public Car(InetSocketAddress socketAddress, CarConfiguration configuration) {
        this.socketAddress = socketAddress;
        this.carConfiguration = configuration;
        this.route = routingFactory.getRoutingType(carConfiguration.ROUTETYPE).getRouteAsList();
	    this.flowrate = 0;
	}

	@Override
	public void connect() {
        clientSocket = new Socket();
        try {
            clientSocket.connect(socketAddress);
        } catch (IOException e) {
	        System.err.println("[Car] cannot connect Host " + e.getMessage());
        }

	}

    public int getFlowrate() {
        return flowrate;
    }

	public void close() throws IOException {
		if(clientSocket != null){
		    done = true;
            clientSocket.close();
        }
	}

    /**
     * Send MAXELEMENTS Coordinates to Server
     */
	public void send() {
        int deliveredElements = 0;
        do{
            for (Coordinate coordinate : route) {
                sendData(coordinate);
                deliveredElements ++;
            }
        }while(deliveredElements < MAXELEMENTS && !done);
	}

    /**
     *
     * @param elements number of coordinates which been sended
     */
	public void send(int elements){
        int deliveredElements = 0;
        do{
            for (Coordinate coordinate : route) {
                sendData(coordinate);
                deliveredElements ++;
            }
        }while(deliveredElements < elements);
    }

    /**
     * Send Coordinate via Outputstream to Server
     * @param coordinate which is sended
     */
	private void sendData(Coordinate coordinate){
		try {
			// send
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			oos.writeObject(coordinate);
			oos.flush();
			// receive
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			Coordinate c = (Coordinate) ois.readObject();
			// increment flowrate for analysis
             ++flowrate;
            Thread.sleep(carConfiguration.DELAY.toMillis());

		} catch (InterruptedException | IOException | ClassNotFoundException ignore) {
        }
    }
}
