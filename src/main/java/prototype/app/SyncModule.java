package prototype.app;
import com.google.inject.AbstractModule;
import prototype.endpoints.ICar;
import prototype.endpoints.IServer;
import prototype.endpoints.classicCarImpl.Car;
import prototype.endpoints.classicServerImpl.Server;

import java.net.InetSocketAddress;

public class SyncModule extends AbstractModule {
	@Override
	protected void configure() {
		int PORT = 1337;
		bind(IServer.class).to(Server.class);
		bind(InetSocketAddress.class).toInstance(new InetSocketAddress(PORT));

		bind(ICar.class).to(Car.class);
	}
}
