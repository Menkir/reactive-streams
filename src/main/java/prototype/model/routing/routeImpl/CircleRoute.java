package prototype.model.routing.routeImpl;

import prototype.model.Coordinate;
import prototype.model.routing.IRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Arrays;

public class CircleRoute implements IRoute {
	@Override
	public Flux<Coordinate> getRoute() {
		Coordinate[] circle = {
				new Coordinate(3,1),
				new Coordinate(4,1),
				new Coordinate(5,1),
				new Coordinate(6,1),
				new Coordinate(7,2),
				new Coordinate(8,3),
				new Coordinate(8,4),
				new Coordinate(8,5),
				new Coordinate(8,6),
				new Coordinate(7,7),
				new Coordinate(6,8),
				new Coordinate(5,8),
				new Coordinate(4,8),
				new Coordinate(3,8),
				new Coordinate(2,7),
				new Coordinate(1,6),
				new Coordinate(1,5),
				new Coordinate(1,4),
				new Coordinate(1,3),
				new Coordinate(2,2),
		};

		return Flux.<Coordinate>create(sink -> {
			while(true){
				Arrays.stream(circle)
						.forEach(sink::next);
			}
		}, FluxSink.OverflowStrategy.DROP);

	}
}
