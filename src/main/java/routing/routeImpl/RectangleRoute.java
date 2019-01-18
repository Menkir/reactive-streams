package routing.routeImpl;

import prototype.model.Coordinate;
import reactor.core.publisher.Flux;
import routing.IRoute;

import java.util.Stack;

public class RectangleRoute implements IRoute {
	@Override
	public Flux<Coordinate> getRoute() {
		int maxSize = 10;
		// collect alle necessary coordinates
		Stack<Coordinate> north = new Stack<>();
		Stack<Coordinate> east = new Stack<>();
		Stack<Coordinate> south = new Stack<>();
		Stack<Coordinate> west =  new Stack<>();

		for(int i = 0 ; i < maxSize; i++){
			north.push(new Coordinate(i,0));
			east.push(new Coordinate(maxSize-1, i));
		}

		for(int i = maxSize-1 ; i >= 0; i--){
			south.push(new Coordinate(i,maxSize-1));
			west.push(new Coordinate(0, i));
		}

		return Flux.fromStream(north.stream())
				.concatWith(Flux.fromStream(east.stream()))
				.concatWith(Flux.fromStream(south.stream()))
				.concatWith(Flux.fromStream(west.stream()));
	}
}
