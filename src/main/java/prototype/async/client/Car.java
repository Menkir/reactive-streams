package prototype.async.client;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import prototype.interfaces.ICar;
import prototype.utility.Serializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import prototype.routing.RoutingFactory;
import java.net.InetSocketAddress;
import java.time.Duration;

public class Car implements ICar {
	private final InetSocketAddress socketAddress;
	private final RoutingFactory routingFactory = new RoutingFactory();
    private RSocket client;
    private Disposable serverEndpoint;
    private final CarConfiguration carConfiguration;
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
				.block();
	}

    @Override
    public void send() {
        serverEndpoint = client.requestChannel(
                Flux.fromIterable(routingFactory.getRoutingType(carConfiguration.ROUTETYPE).getRouteAsList())
                        .repeat(100_000) // emit 400_000 Coordinates
                        .delayElements(carConfiguration.DELAY)
                        .doOnNext(coordinate -> coordinate.setSignalPower(((int) (Math.random() * 10))))
                        .map(coordinate -> DefaultPayload.create(Serializer.serialize(coordinate)))
                        .publish().autoConnect(0)
                        .share()
        ).subscribe(payload -> ++flowrate);
    }

    @Override
    public void close() {
        serverEndpoint.dispose();
    }
}
