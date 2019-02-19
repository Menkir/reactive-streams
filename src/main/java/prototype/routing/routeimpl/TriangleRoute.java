package prototype.routing.routeimpl;

import prototype.model.Measurement;
import prototype.routing.IRoute;

import java.util.ArrayList;
import java.util.List;

public class TriangleRoute implements IRoute {
    @Override
	public List<Measurement> getRouteAsList() {
		ArrayList<Measurement> measurements = new ArrayList<>();
        int maxsize = 10;
        for(int i = maxsize -1; i>= 0; --i)
			measurements.add(new Measurement(maxsize -1, i));
		for(int i = 0; i< maxsize; ++i)
			measurements.add(new Measurement(maxsize -i-1, i));
		for(int i = 0; i< maxsize; ++i)
			measurements.add(new Measurement(i, maxsize -1));
		return measurements;
	}
}
