package prototype.client;

import routing.RoutingFactory.RouteType;

public class ClientConfigurator {
	private int emittingDelay = 0; //with which delay the data is emitted
	private RouteType routeType; // Rectangle, Square etc.
	private int amountOfLoops = 1; // Number of how much Iterations a full Route need.

	public ClientConfigurator(){

	}
}
