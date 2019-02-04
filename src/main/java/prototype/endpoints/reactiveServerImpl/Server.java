package prototype.endpoints.reactiveServerImpl;

import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import prototype.model.Coordinate;
import prototype.endpoints.IServer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.WorkQueueProcessor;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Scanner;

public class Server implements IServer {
	private InetSocketAddress socketAddress;
	private static final RSocketImpl rSocket = new RSocketImpl();
	private Disposable channel;

	@Inject
	public Server(InetSocketAddress socketAddress){
		this.socketAddress = socketAddress;
	}

	@Override
	public void receive() {
		channel = RSocketFactory.receive()
				.acceptor((setupPayload, reactiveSocket) ->
						Mono.just(rSocket))
				.transport(TcpServerTransport.create(socketAddress.getHostName(), socketAddress.getPort()))
                .start()
				.subscribe();
	}

	public void dispose(){
		channel.dispose();
	}
	public WorkQueueProcessor<Flux<Payload>> getChannels(){
		return rSocket.getChannels();
	}

	public static void main(final String... args){
	    Server server = new Server(new InetSocketAddress("127.0.0.1", 1337));
	    server.receive();
	    Scanner scanner = new Scanner(System.in);

	    while(scanner.hasNext()){}
	    server.dispose();
    }
}
