package prototype.async.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import prototype.interfaces.ICar;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import prototype.routing.RoutingFactory;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Car implements ICar {
	private final InetSocketAddress socketAddress;
	private final RoutingFactory routingFactory = new RoutingFactory();
    private RSocket client;
    private Flux<Payload> serverEndpoint;
    private final CarConfiguration carConfiguration;
    private final ExecutorService executors = Executors.newFixedThreadPool(4);
    private final Scheduler scheduler = Schedulers.fromExecutor(executors);
	private int flowrate = 0;

    public Car(InetSocketAddress socketAddress) {
		this.carConfiguration = new CarConfiguration();
		this.socketAddress =  socketAddress;
    }

    public Car(InetSocketAddress socketAddress, CarConfiguration configuration) {
	    this.socketAddress = socketAddress;
	    this.carConfiguration = configuration;
    }

	@Override
	public void connect() {
		this.client = RSocketFactory
				.connect()
				.keepAliveAckTimeout(Duration.ofMinutes(30))
				.transport(TcpClientTransport.create(socketAddress.getHostName(), socketAddress.getPort()))
				.start()
				.subscribeOn(scheduler)
				.publishOn(scheduler)
				.block();
	}

	public void requestChannel() {
		serverEndpoint = client.requestChannel(
				Flux.fromIterable(routingFactory.getRoutingType(carConfiguration.ROUTETYPE).getRouteAsList())
                        .repeat(100_000)// emit 400kk Coordinates
						.delayElements(carConfiguration.DELAY)
                        .doOnNext(coordinate -> coordinate.setSignalPower(((int) (Math.random() * 10))))
						.map(coordinate -> DefaultPayload.create(Serializer.serialize(coordinate)))
						.publish().autoConnect(0)
                        .share()
		);
	}

	public Disposable subscribeOnServerEndpoint(){
		return serverEndpoint.subscribe(payload -> {
			Coordinate data = Serializer
					.deserialize(payload);
			++flowrate;
		});
	}

    public int getFlowrate() {
        return flowrate;
    }

	public void shutDown(){
        executors.shutdown();
        scheduler.dispose();
	}
}
