package prototype.async.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.reactivestreams.Publisher;
import prototype.interfaces.IServer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Scanner;

public class CarServer implements IServer {
	private InetSocketAddress socketAddress;
	private Disposable channel;

	private CarServer(InetSocketAddress socketAddress){
		this.socketAddress = socketAddress;
	}

	@Override
	public void receive() {
		channel = RSocketFactory.receive()
				.acceptor((setupPayload, reactiveSocket) ->
						Mono.just(new AbstractRSocket() {
							@Override
							public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
								return Flux.from(payloads)
										.flatMapSequential(
												payload -> Flux.just(payload).delaySequence(Duration.ofMillis(10)));
							}
						}))
				.transport(TcpServerTransport.create(socketAddress.getHostName(), socketAddress.getPort()))
				.start()
				.block();
	}

	public void close(){
		channel.dispose();
	}

	public static void main(final String... args){
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
