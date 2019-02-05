package prototype.routing;

import prototype.routing.routeImpl.CircleRoute;
import prototype.routing.routeImpl.RectangleRoute;
import prototype.routing.routeImpl.TriangleRoute;

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
