package prototype.sync.server;
import prototype.interfaces.IServer;
import prototype.model.Coordinate;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarServer implements IServer {
	private final InetSocketAddress socketAddress;
	private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(8);

	private CarServer(InetSocketAddress socketAddress){
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
					System.err.println("[CarServer] " + e.getMessage());
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
							System.err.println("[CarServer] Connection to Client was dropped " + e.getMessage());
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
		executorService.shutdown();
	}

	public static void main(final String... args) throws IOException {
		Scanner sc = new Scanner(System.in);
		CarServer carServer = new CarServer(new InetSocketAddress("127.0.0.1", 1337));
		carServer.receive();

		System.out.println("Type 'close' to terminate the CarServer:");
		while(true){
			String input = sc.nextLine();
			switch(input){
				case "close": carServer.close();
					return;
				default:
					System.err.println("Try again...");
			}
		}
	}
}
