package prototype.routing;

import prototype.model.Coordinate;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.List;

public interface IRoute {
    /**
     * Blocking Variant of getRoute()
     * @return List of Coordinates
     */
	List<Coordinate> getRouteAsList();

    /**
     * A infinite Stream of 0,0 Coordinates
     * For experimental purpose
     * @return Flux with Coordinates
     */
    default Flux<Coordinate> getRoute() {
        return Flux.interval(Duration.ZERO)
				.onBackpressureDrop()
		        .map(n -> new Coordinate(0,0));
    }
}
