package prototype.routing;

import prototype.model.Coordinate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.List;

public interface IRoute {

    int SENDINGTIME = 30 * 1_000;

    /**
     * Blocking Variant of getRoute()
     * @return List of Coordinates
     */
	List<Coordinate> getRouteAsList();

    /**
     * A Route on a map, represented as infinite reactive Stream of coordinates
     * Because infinite Streams are bad for benchmarking the Streams emitting element for 30 seconds
     * @return Flux with Coordinates
     * @throws InterruptedException
     */
    default Flux<Coordinate> getRoute() throws InterruptedException {
        return Flux.<Coordinate>create(sink -> {
            long before = System.currentTimeMillis();
            long after = 0;
            while((after - before) < SENDINGTIME) {
                getRouteAsList()
                        .forEach(sink::next);
                after = System.currentTimeMillis();
            }
        }, FluxSink.OverflowStrategy.DROP);
    }
}
