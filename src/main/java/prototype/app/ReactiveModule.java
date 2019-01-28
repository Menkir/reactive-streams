package prototype.app;

import com.google.inject.AbstractModule;
import prototype.endpoints.ICar;
import prototype.endpoints.IServer;
import prototype.endpoints.carImpl.Car;
import prototype.endpoints.carImpl.CarConfiguration;
import prototype.endpoints.serverImpl.Server;
import prototype.routing.RoutingFactory;

import java.time.Duration;

public class EndpointsModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(IServer.class).to(Server.class);
		bind(ICar.class).to(Car.class);
		bind(CarConfiguration.class).toInstance(new CarConfiguration(
				Duration.ofMillis(200),
				RoutingFactory.RouteType.CIRCLE
		));
	}
}
