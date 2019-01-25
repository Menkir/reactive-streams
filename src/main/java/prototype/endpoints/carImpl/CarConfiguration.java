package prototype.endpoints.carImpl;
import prototype.routing.RoutingFactory.RouteType;
import java.time.Duration;

public class CarConfiguration {
	final Duration DELAY;//with which delay the data is emitted
	final RouteType ROUTETYPE; // Rectangle, Square etc.

	public CarConfiguration(){
		this.DELAY = Duration.ZERO;
		this.ROUTETYPE = RouteType.RECTANGLE;
	}

	public CarConfiguration(Duration DELAY, RouteType ROUTETYPE){
		this.DELAY = DELAY;
		this.ROUTETYPE = ROUTETYPE;
	}
}
