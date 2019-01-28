package prototype.app;

import com.google.inject.AbstractModule;
import prototype.endpoints.ICar;
import prototype.endpoints.IServer;
import prototype.endpoints.carImpl.Car;
import prototype.endpoints.carImpl.CarConfiguration;
import prototype.endpoints.serverImpl.Server;
import prototype.routing.RoutingFactory;

import java.net.InetSocketAddress;
import java.time.Duration;

public class ReactiveModule extends AbstractModule {
	@Override
	protected void configure() {
		final int PORT = 1337;

		bind(IServer.class).to(Server.class);
		bind(InetSocketAddress.class).toInstance(new InetSocketAddress(PORT));

		bind(ICar.class).to(Car.class);
		bind(CarConfiguration.class).toInstance(new CarConfiguration(
				Duration.ofMillis(200),
				RoutingFactory.RouteType.CIRCLE
		));
	}
}
