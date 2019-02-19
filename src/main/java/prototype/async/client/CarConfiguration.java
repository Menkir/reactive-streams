package prototype.async.client;
import prototype.routing.RoutingFactory.RouteType;
import java.time.Duration;

public class CarConfiguration {
	/**
	 * The Duration DELAY Variable specify the delay between two emitted Measurements.
	 * The RouteType is an Enumeration to configure the Route.
	 */
	public final Duration DELAY;
	public final RouteType ROUTETYPE;

	/**
	 * Initialize DELAY and ROUTETYPE with default values.
	 */
	public CarConfiguration(){
		this.DELAY = Duration.ZERO;
		this.ROUTETYPE = RouteType.RECTANGLE;
	}

	/**
	 * Initialize DELAY and ROUTETYPE with custom values.
	 */
	public CarConfiguration(Duration DELAY, RouteType ROUTETYPE){
		this.DELAY = DELAY;
		this.ROUTETYPE = ROUTETYPE;
	}
}
