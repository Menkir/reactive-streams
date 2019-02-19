package prototype.routing.routeimpl;

import prototype.model.Measurement;
import prototype.routing.IRoute;

import java.util.Arrays;
import java.util.List;

public class CircleRoute implements IRoute {
	@Override
	public List<Measurement> getRouteAsList() {
		Measurement[] circle = {
				new Measurement(3,1),
				new Measurement(4,1),
				new Measurement(5,1),
				new Measurement(6,1),
				new Measurement(7,2),
				new Measurement(8,3),
				new Measurement(8,4),
				new Measurement(8,5),
				new Measurement(8,6),
				new Measurement(7,7),
				new Measurement(6,8),
				new Measurement(5,8),
				new Measurement(4,8),
				new Measurement(3,8),
				new Measurement(2,7),
				new Measurement(1,6),
				new Measurement(1,5),
				new Measurement(1,4),
				new Measurement(1,3),
				new Measurement(2,2),
		};
		return Arrays.asList(circle);
	}
}
