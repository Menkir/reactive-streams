package prototype.endpoints.carImpl;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import prototype.endpoints.ICar;
import prototype.model.Coordinate;
import prototype.utility.Serializer;
import reactor.core.publisher.Flux;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import prototype.routing.RoutingFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Car implements ICar {
	private final int PORT = 1337;
	private final String HOST = "127.0.0.1";

	private final RoutingFactory routingFactory = new RoutingFactory();
    private RSocket client;
    private Flux<Payload> serverEndpoint;
    private final CarConfiguration carConfiguration;
    private final Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));


    public Car() throws InterruptedException {
		this.carConfiguration = new CarConfiguration();
    }

    @Inject
    public Car(CarConfiguration configuration) throws InterruptedException {
    	this.carConfiguration = configuration;
    	//this.scheduler = Schedulers.parallel(); // needs adaptions on CarConfiguration
    }

	@Override
	public void connect() {
		this.client = RSocketFactory
				.connect()
				.transport(TcpClientTransport.create(HOST, PORT))
				.start()
				.subscribeOn(scheduler)
				.publishOn(scheduler)
				.block();
	}

	public void requestChannel() throws InterruptedException {
		serverEndpoint = client.requestChannel(
				Flux.from(routingFactory.getRoutingType(carConfiguration.ROUTETYPE).getRoute())
						.delayElements(carConfiguration.DELAY)
						.subscribeOn(scheduler)
						.doOnNext(coordinate -> coordinate.setSignalPower(((int) (Math.random() * 10))))
						.map(coordinate -> DefaultPayload.create(Serializer.serialize(coordinate)))
						.publishOn(scheduler)
						.share()
		);
	}

	public void subscribeOnServerEndpoint(){
		serverEndpoint.subscribeOn(scheduler).publishOn(scheduler).subscribe(payload -> {
			Coordinate data = Serializer
					.deserialize(payload);
		    System.out.println("\t\t\t\t\t\t" + Thread.currentThread().getName()
				    + " [LOG] received from Server " + data);
		});
	}

	public static void main(final String... args) throws InterruptedException {
		new Car();
	}
}
