package prototype.async.client;
import prototype.routing.RoutingFactory.RouteType;
import java.time.Duration;

public class CarConfiguration {
	public final Duration DELAY;//with which delay the data is emitted
	public final RouteType ROUTETYPE; // Rectangle, Square etc.

	public CarConfiguration(){
		this.DELAY = Duration.ZERO;
		this.ROUTETYPE = RouteType.RECTANGLE;
	}

	public CarConfiguration(Duration DELAY, RouteType ROUTETYPE){
		this.DELAY = DELAY;
		this.ROUTETYPE = ROUTETYPE;
	}
}
