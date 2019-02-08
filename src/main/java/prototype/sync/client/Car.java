package prototype.sync.client;

import prototype.async.client.CarConfiguration;
import prototype.interfaces.ICar;
import prototype.model.Coordinate;
import prototype.routing.RoutingFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Car implements ICar {
	private final InetSocketAddress socketAddress;
	private Socket clientSocket;
	private CompletableFuture<Void> clientThread;
    private CarConfiguration carConfiguration;
    private final int MAXELEMENTS = 100_000_000;
    private RoutingFactory routingFactory = new RoutingFactory();
    private List<Coordinate> route;
    private int flowrate = 0;


	public Car(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
		carConfiguration = new CarConfiguration();
        this.route = routingFactory.getRoutingType(carConfiguration.ROUTETYPE).getRouteAsList();
	}

    public Car(InetSocketAddress socketAddress, CarConfiguration configuration) {
        this.socketAddress = socketAddress;
        this.carConfiguration = configuration;
        this.route = routingFactory.getRoutingType(carConfiguration.ROUTETYPE).getRouteAsList();
    }

	@Override
	public void connect() {
        clientSocket = new Socket();
        try {
            clientSocket.connect(socketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    public int getFlowrate() {
        return flowrate;
    }

	public void close() throws IOException, InterruptedException {
		clientSocket.close();
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
        }while(deliveredElements < MAXELEMENTS);


		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send Coordinate via Outputstream to Server
     * @param coordinate which is sended
     */
	private void sendData(Coordinate coordinate){
		try {
			// send
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(coordinate);
			oos.flush();

			// receive
			ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
			Coordinate c = (Coordinate) ois.readObject();

			// increment flowrate for analysis
             ++flowrate;

            Thread.sleep(carConfiguration.DELAY.toMillis());

		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
    }

    public void resetFlowRate(){
	    flowrate = 0;
    }
}
