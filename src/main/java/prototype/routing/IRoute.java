package prototype.routing;

import prototype.model.Coordinate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.List;

public interface IRoute {

    int MAXEMITTED = 100_000_000;

    /**
     * Blocking Variant of getRoute()
     * @return List of Coordinates
     */
	List<Coordinate> getRouteAsList();

    /**
     * A Route on a map, represented as infinite reactive Stream of coordinates
     * Because infinite Streams are bad for benchmarking the Streams emitting element for 30 seconds
     * @return Flux with Coordinates
     */
    default Flux<Coordinate> getRoute() {
        return Flux.<Coordinate>create(sink -> {
            int emitted = 0;
            while(emitted < MAXEMITTED) {
                for(Coordinate coordinate : getRouteAsList()){
                    sink.next(coordinate);
                    emitted++;
                }
            }
            sink.complete();
        }, FluxSink.OverflowStrategy.DROP);
    }
}
