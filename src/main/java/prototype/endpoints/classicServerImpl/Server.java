package prototype.endpoints.classicServerImpl;
import prototype.endpoints.IServer;
import prototype.model.Coordinate;

import javax.inject.Inject;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Observable implements IServer  {
	private final InetSocketAddress socketAddress;
	private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private static ArrayList<Long> clientServedTimes = new ArrayList<>();

    @Inject
	public Server(InetSocketAddress socketAddress){
		this.socketAddress = socketAddress;
	}

	@Override
	public void receive() throws IOException {
		this.serverSocket = new ServerSocket();
		serverSocket.bind(socketAddress);
		CompletableFuture.runAsync(() -> {
			while(!serverSocket.isClosed()){
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
					//System.out.println("[SERVER] Accept Connection");
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
							//System.out.println("[SERVER] " + finalClientSocket.hashCode() + " Receive " + coordinate);
							setChanged();
							notifyObservers(new Tuple<>(finalClientSocket.getPort(), coordinate));
                            oos = new ObjectOutputStream(finalClientSocket.getOutputStream());
                            oos.writeObject(coordinate);
                            oos.flush();
						} catch (EOFException e) {
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

	public class Tuple<T, E>{
		private T t;
		private E e;
		public Tuple(T t, E e){
			this.t = t;
			this.e = e;
		}

		public T getT() {
			return t;
		}

		public E getE() {
			return e;
		}
	}

	public void close() throws IOException {
		serverSocket.close();
	}

	public static void main(final String... args){
	    Scanner sc = new Scanner(System.in);
		try {
			new Server(new InetSocketAddress("10.168.10.155", 1337)).receive();
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
