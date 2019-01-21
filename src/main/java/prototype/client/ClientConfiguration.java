package prototype.client;
import routing.RoutingFactory.RouteType;
import java.time.Duration;

public class ClientConfiguration {
	final Duration DELAY;//with which delay the data is emitted
	final RouteType ROUTETYPE; // Rectangle, Square etc.
	final int LOOPS; // Number of how much Iterations a full Route need.

	public ClientConfiguration(){
		this.LOOPS = 1;
		this.DELAY = Duration.ZERO;
		this.ROUTETYPE = RouteType.RECTANGLE;
	}

	public ClientConfiguration(Duration DELAY, RouteType ROUTETYPE, int LOOPS){
		this.LOOPS = LOOPS;
		this.ROUTETYPE = ROUTETYPE;
		this.DELAY = DELAY;
	}
}
