package prototype.routing.routeimpl;

import prototype.model.Coordinate;
import prototype.routing.IRoute;

import java.util.ArrayList;
import java.util.List;

public class TriangleRoute implements IRoute {
    @Override
	public List<Coordinate> getRouteAsList() {
		ArrayList<Coordinate> coordinates = new ArrayList<>();
        int maxsize = 10;
        for(int i = maxsize -1; i>= 0; --i)
			coordinates.add(new Coordinate(maxsize -1, i));
		for(int i = 0; i< maxsize; ++i)
			coordinates.add(new Coordinate(maxsize -i-1, i));
		for(int i = 0; i< maxsize; ++i)
			coordinates.add(new Coordinate(i, maxsize -1));
		return coordinates;
	}
}
