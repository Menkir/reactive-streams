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
import routing.RoutingFactory;
import java.time.Duration;
import java.util.concurrent.Executors;

import static routing.RoutingFactory.RouteType.RECTANGLE;


public final class Client {
    private final RSocket client;
    private final Flux<Payload> serverEndpoint;
    public Client() {
	    RoutingFactory routingFactory = new RoutingFactory();

        final int port = 1337;
        Scheduler clientScheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(8));
        this.client = RSocketFactory.connect()
                .transport(TcpClientTransport.create("127.0.0.1", port))
                .start().subscribeOn(Schedulers.parallel()).block();
        assert client != null;
        serverEndpoint = client.requestChannel(
        		        Flux.interval(Duration.ofMillis(100))
				        .subscribeOn(clientScheduler)
		                .take(3)
		                .map(number -> routingFactory.getRoutingType(RECTANGLE).getRoute())
				        .flatMap(flux-> flux)
				        .map(coordinate -> DefaultPayload.create(Serializer.serialize(coordinate)))
				        .publishOn(clientScheduler)
				        .share()
        );
        serverEndpoint.subscribe(payload -> {
            Coordinate data = Serializer
                                .deserialize(payload.getData().array());
            System.out.println(getTabs() + Thread.currentThread().getName()
                    + " [LOG] received from Server " + data);
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
