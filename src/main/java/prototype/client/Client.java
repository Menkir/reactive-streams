package prototype.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import prototype.routing.RoutingFactory;

import java.util.concurrent.Executors;

public class Client {
	private final int PORT = 1337;
	private final String HOST = "127.0.0.1";

	private final RoutingFactory routingFactory = new RoutingFactory();
    private RSocket client;
    private Flux<Payload> serverEndpoint;

    public Client() {
		start(new ClientConfiguration());
    }

    public Client(ClientConfiguration configuration){
    	start(configuration);
    }

    private void start(ClientConfiguration configuration){
    	Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));
	    this.client = RSocketFactory.connect()
			    .transport(TcpClientTransport.create(HOST, PORT))
			    .start()
			    .subscribeOn(scheduler)
			    .publishOn(scheduler)
			    .block();
	    assert client != null;

	    serverEndpoint = client.requestChannel(
			    Flux.from(routingFactory.getRoutingType(configuration.ROUTETYPE).getRoute())
					.subscribeOn(scheduler)
					.map(coordinate -> DefaultPayload.create(Serializer.serialize(coordinate)))
					.delayElements(configuration.DELAY)
				    .publishOn(scheduler)
					.share()
	    );

	    serverEndpoint.subscribeOn(scheduler).publishOn(scheduler).subscribe(payload -> {
		    Coordinate data = Serializer
				    .deserialize(payload);
		    /*System.out.println(getTabs() + Thread.currentThread().getName()
				    + " [LOG] received from Server " + data);*/
	    });
    }

    private String getTabs(){
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i< 20; ++i)
    		sb.append('\t');
    	return sb.toString();
	}

    public void dispose() {
        this.client.dispose();
    }

    public Flux<Payload> getServerEndpoint() {
        return this.serverEndpoint;
    }

}
