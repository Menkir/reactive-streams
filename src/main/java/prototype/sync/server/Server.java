package prototype.sync.server;
import prototype.interfaces.IServer;
import prototype.model.Coordinate;
import prototype.utility.Tuple2;
import java.io.*;
import java.net.*;
import java.rmi.server.UID;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Observable implements IServer  {
	private final InetSocketAddress socketAddress;
	private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private static ArrayList<Long> clientServedTimes = new ArrayList<>();

	public Server(InetSocketAddress socketAddress){
		this.socketAddress = socketAddress;
	}

	@Override
	public void receive() throws IOException {
		this.serverSocket = new ServerSocket();
		serverSocket.bind(socketAddress);
		CompletableFuture.runAsync(() -> {
			UID carUid = new UID();
			while(!serverSocket.isClosed()){
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
					// update observer and spawn client graphic
					setChanged();
					notifyObservers(carUid);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Socket finalClientSocket = clientSocket;
				CompletableFuture.runAsync(() -> {
					ObjectInputStream ois = null;
					ObjectOutputStream oos = null;
					long before, after = 0;
					before = System.currentTimeMillis();
					while(true) try {
						try {
							assert finalClientSocket != null;
							ois = new ObjectInputStream(finalClientSocket.getInputStream());
							Coordinate coordinate = (Coordinate) ois.readObject();
                            oos = new ObjectOutputStream(finalClientSocket.getOutputStream());
                            oos.writeObject(coordinate);
                            oos.flush();

							// update subobserver and change position of client graphic
							setChanged();
							notifyObservers(new Tuple2<>(carUid, coordinate));
						} catch (EOFException | SocketException e) {
							break;
						}

					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
					after = System.currentTimeMillis();
                    clientServedTimes.add((after -before));
				}, executorService);
			}

		}, executorService);

	}

	public void close() throws IOException {
		serverSocket.close();
	}

	public static void main(final String... args){
	    Scanner sc = new Scanner(System.in);
		try {
			new Server(new InetSocketAddress("192.168.0.199", 1338)).receive();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(sc.hasNext()){

        }
        long times = clientServedTimes.size();
		long sum = clientServedTimes.stream().reduce(Math::addExact).get();
        System.out.println("Server Bearbeitungszeit: " + (sum/(double)times));
	}
}
