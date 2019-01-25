package prototype.endpoints.serverImpl;

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

public class Server implements IServer {
	private final int PORT = 1337;
	private final String HOST = "127.0.0.1";
	private Disposable channel;
	public final Coordinate signalTower = new Coordinate(2,1);
	private final WorkQueueProcessor <Flux<Payload>> channels;

	@Inject
	public Server(){
		channels = WorkQueueProcessor.create();
	}

	@Override
	public void receive() {
		this.channel = RSocketFactory.receive()
				.acceptor((setupPayload, reactiveSocket) ->
						Mono.just(new RSocketImpl()))
				.transport(TcpServerTransport.create(HOST, PORT))
				.start()
				.subscribe();
	}
}
