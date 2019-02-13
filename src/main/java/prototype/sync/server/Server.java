package prototype.sync.server;
import prototype.interfaces.IServer;
import prototype.model.Coordinate;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements IServer {
	private final InetSocketAddress socketAddress;
	private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(8);
    private static ArrayList<Long> clientServedTimes = new ArrayList<>();

	public Server(InetSocketAddress socketAddress){
		this.socketAddress = socketAddress;
	}

	@Override
	public void receive() throws IOException {
		this.serverSocket = new ServerSocket();
		serverSocket.bind(socketAddress);
		CompletableFuture.runAsync(() -> {
			while(!serverSocket.isClosed()){
				Socket clientSocket;
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					System.err.println("[Server] stopped" + e.getMessage());
					return;
				}

				Socket finalClientSocket = clientSocket;
				CompletableFuture.runAsync(() -> {
					ObjectInputStream ois;
					ObjectOutputStream oos;
					while(true) {
						try {
							assert finalClientSocket != null;
							ois = new ObjectInputStream(new BufferedInputStream(finalClientSocket.getInputStream()));
							Coordinate coordinate = (Coordinate) ois.readObject();

							// do expensive work
							Thread.sleep(10);

							oos = new ObjectOutputStream(new BufferedOutputStream(finalClientSocket.getOutputStream()));
							oos.writeObject(coordinate);
							oos.flush();
						} catch (IOException | ClassNotFoundException | InterruptedException e) {
							System.err.println("[Server] Connection to Client was dropped " + e.getMessage());
							break;
						}

					}
				}, executorService);

			}
		}, executorService);

	}

	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(final String... args){
	    Scanner sc = new Scanner(System.in);
		try {
			new Server(new InetSocketAddress("192.168.0.199", 1337)).receive();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(sc.hasNext()){

        }
	}
}
