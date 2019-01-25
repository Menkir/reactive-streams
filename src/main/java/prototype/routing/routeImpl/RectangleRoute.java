package prototype.routing.routeImpl;

import prototype.model.Coordinate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import prototype.routing.IRoute;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RectangleRoute implements IRoute {
	@Override
	public Flux<Coordinate> getRoute() {
		return Flux.<Coordinate>create(sink -> {
			ArrayList<Coordinate> coordinates = new ArrayList<>();
			int maxSize = 10;
			while(true){
				for(int i = 0 ; i < maxSize; i++){
					coordinates.add(new Coordinate(i,0));
				}
				for(int i = 0 ; i < maxSize; i++){
					coordinates.add(new Coordinate(maxSize-1, i));
				}
				for(int i = maxSize-1 ; i >= 0; i--){
					coordinates.add(new Coordinate(i,maxSize-1));
				}
				for(int i = maxSize-1 ; i >= 0; i--){
					coordinates.add(new Coordinate(0, i));
				}

				coordinates.forEach(sink::next);

			}
		}, FluxSink.OverflowStrategy.DROP);
	}
}
