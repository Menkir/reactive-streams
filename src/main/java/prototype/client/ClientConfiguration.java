package prototype.client;
import routing.RoutingFactory.RouteType;
import java.time.Duration;

public class ClientConfiguration {
	final Duration DELAY;//with which delay the data is emitted
	final RouteType ROUTETYPE; // Rectangle, Square etc.

	public ClientConfiguration(){
		this.DELAY = Duration.ZERO;
		this.ROUTETYPE = RouteType.RECTANGLE;
	}

	public ClientConfiguration(Duration DELAY, RouteType ROUTETYPE){
		this.DELAY = DELAY;
		this.ROUTETYPE = ROUTETYPE;
	}
}
