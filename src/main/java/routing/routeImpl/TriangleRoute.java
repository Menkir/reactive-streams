package routing.routeImpl;

import prototype.model.Coordinate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import routing.IRoute;
import java.time.Duration;

public class TriangleRoute implements IRoute {
	@Override
	public Flux<Coordinate> getRoute() {
		int maxsize = 10;
		return Flux.<Coordinate>create(sink -> {
			while(true){
				for(int i = maxsize-1; i>= 0; --i)
					sink.next(new Coordinate(maxsize-1, i));

				for(int i = 0; i< maxsize; ++i)
					sink.next(new Coordinate(maxsize-i-1, i));

				for(int i = 0; i< maxsize; ++i)
					sink.next(new Coordinate(i, maxsize-1));
			}
		}, FluxSink.OverflowStrategy.DROP).delayElements(Duration.ofMillis(100));
	}
}
