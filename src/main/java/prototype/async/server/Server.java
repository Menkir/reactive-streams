package prototype.async.server;

import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import prototype.interfaces.IServer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.WorkQueueProcessor;
import java.net.InetSocketAddress;

public class Server implements IServer {
	private InetSocketAddress socketAddress;
	private static final RSocketImpl rSocket = new RSocketImpl();
	private Disposable channel;

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
		rSocket.dispose();
	}
	public WorkQueueProcessor<Flux<Payload>> getChannels(){
		return rSocket.getChannels();
	}
}
