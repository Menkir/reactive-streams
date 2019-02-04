package prototype.endpoints.reactiveCarImpl;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import prototype.endpoints.ICar;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import prototype.routing.RoutingFactory;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Car implements ICar {
	private final InetSocketAddress socketAddress;
	private final RoutingFactory routingFactory = new RoutingFactory();
    private RSocket client;
    private Flux<Payload> serverEndpoint;
    private final CarConfiguration carConfiguration;
    private final Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));

	public int getFlowrate() {
		return flowrate;
	}

	private int flowrate = 0;

    public Car(InetSocketAddress socketAddress) {
		this.carConfiguration = new CarConfiguration();
		this.socketAddress =  socketAddress;
    }

    @Inject
    public Car(InetSocketAddress socketAddress, CarConfiguration configuration) {
	    this.socketAddress = socketAddress;
	    this.carConfiguration = configuration;
    }

	@Override
	public void connect() {
		this.client = RSocketFactory
				.connect()
				.transport(TcpClientTransport.create(socketAddress.getHostName(), socketAddress.getPort()))
				.start()
				.subscribeOn(scheduler)
				.publishOn(scheduler)
				.block();
	}

	public void requestChannel() throws InterruptedException {
		serverEndpoint = client.requestChannel(
				Flux.from(routingFactory.getRoutingType(carConfiguration.ROUTETYPE).getRoute())
                        .onBackpressureBuffer(Integer.MAX_VALUE)
						.delayElements(carConfiguration.DELAY)
						.subscribeOn(scheduler)
						.doOnNext(coordinate -> coordinate.setSignalPower(((int) (Math.random() * 10))))
						.map(coordinate -> DefaultPayload.create(Serializer.serialize(coordinate)))
						.publishOn(scheduler)
						.share()
		);
	}

	public Disposable subscribeOnServerEndpoint(){
		return serverEndpoint.subscribeOn(scheduler).publishOn(scheduler).subscribe(payload -> {
			Coordinate data = Serializer
					.deserialize(payload);
			++flowrate;
		});
	}
}
