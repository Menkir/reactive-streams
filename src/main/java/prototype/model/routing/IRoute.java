package prototype.routing;

import prototype.model.Coordinate;
import reactor.core.publisher.Flux;

public interface IRoute {
	Flux<Coordinate> getRoute() throws InterruptedException;
}
