package prototype.routing.routeImpl;

import prototype.model.Coordinate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import prototype.routing.IRoute;
import java.time.Duration;

public class RectangleRoute implements IRoute {
	@Override
	public Flux<Coordinate> getRoute() {
		return Flux.<Coordinate>create(sink -> {
			int maxSize = 10;
			while(true){
				for(int i = 0 ; i < maxSize; i++)
					sink.next(new Coordinate(i,0));
				try {
					IRoute.tick();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for(int i = 0 ; i < maxSize; i++)
					sink.next(new Coordinate(maxSize-1, i));
				try {
					IRoute.tick();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for(int i = maxSize-1 ; i >= 0; i--)
					sink.next(new Coordinate(i,maxSize-1));
				try {
					IRoute.tick();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for(int i = maxSize-1 ; i >= 0; i--)
					sink.next(new Coordinate(0, i));
				try {
					IRoute.tick();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, FluxSink.OverflowStrategy.DROP).delayElements(Duration.ofMillis(100));
	}
}
