package prototype.model.routing;

import prototype.model.routing.routeImpl.CircleRoute;
import prototype.model.routing.routeImpl.RectangleRoute;
import prototype.model.routing.routeImpl.TriangleRoute;

public class RoutingFactory {
	public enum RouteType{
		RECTANGLE,
		TRIANGLE,
		CIRCLE
	}
	public IRoute getRoutingType(RouteType type){
		switch (type){
			case RECTANGLE: return new RectangleRoute();
			case TRIANGLE: return new TriangleRoute();
			case CIRCLE: return new CircleRoute();
			default: return new RectangleRoute();
		}
	}
}
