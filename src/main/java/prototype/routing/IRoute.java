package prototype.routing;

import prototype.model.Measurement;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public interface IRoute {
    /**
     * Blocking Variant of getRoute()
     * @return List of Coordinates
     */
	List<Measurement> getRouteAsList();

    /**
     * A infinite Stream of empty Measurements
     * For experimental purpose
     * @return Flux with Measurement
     */
    default Flux<Measurement> getRoute() {
        return Flux.interval(Duration.ZERO)
				.onBackpressureDrop()
		        .map(n -> new Measurement(0,0));
    }
}
