package prototype.model.routing.routeImpl;

import prototype.model.Coordinate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import prototype.model.routing.IRoute;

import java.util.ArrayList;

public class TriangleRoute implements IRoute {
	@Override
	public Flux<Coordinate> getRoute() {
		ArrayList<Coordinate> coordinates = new ArrayList<>();
		int maxsize = 10;
		return Flux.<Coordinate>create(sink -> {
			while(true){
				for(int i = maxsize-1; i>= 0; --i)
					coordinates.add(new Coordinate(maxsize-1, i));
				for(int i = 0; i< maxsize; ++i)
					coordinates.add(new Coordinate(maxsize-i-1, i));
				for(int i = 0; i< maxsize; ++i)
					coordinates.add(new Coordinate(i, maxsize-1));

				coordinates.forEach(sink::next);
			}
		}, FluxSink.OverflowStrategy.DROP);
	}
}
