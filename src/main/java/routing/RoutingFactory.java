package routing;

import routing.routeImpl.RectangleRoute;

public class RoutingFactory {
	public enum RouteType{
		RECTANGLE
	}
	public IRoute getRoute(RouteType type){
		switch (type){
			case RECTANGLE: return new RectangleRoute();
			default: return new RectangleRoute();
		}
	}
}
