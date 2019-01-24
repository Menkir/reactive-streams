package prototype.routing;

import prototype.routing.routeImpl.RectangleRoute;
import prototype.routing.routeImpl.TriangleRoute;

public class RoutingFactory {
	public enum RouteType{
		RECTANGLE,
		TRIANGLE
	}
	public IRoute getRoutingType(RouteType type){
		switch (type){
			case RECTANGLE: return new RectangleRoute();
			case TRIANGLE: return new TriangleRoute();
			default: return new RectangleRoute();
		}
	}
}
