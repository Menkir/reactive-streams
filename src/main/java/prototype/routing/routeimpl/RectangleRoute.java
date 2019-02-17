package prototype.routing.routeimpl;

import prototype.model.Coordinate;
import prototype.routing.IRoute;

import java.util.ArrayList;
import java.util.List;

public class RectangleRoute implements IRoute {
    @Override
    public List<Coordinate> getRouteAsList() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        int maxSize = 10;

        for (int i = 0; i < maxSize; i++) {
            coordinates.add(new Coordinate(i, 0));
        }
        for (int i = 0; i < maxSize; i++) {
            coordinates.add(new Coordinate(maxSize - 1, i));
        }
        for (int i = maxSize - 1; i >= 0; i--) {
            coordinates.add(new Coordinate(i, maxSize - 1));
        }
        for (int i = maxSize - 1; i >= 0; i--) {
            coordinates.add(new Coordinate(0, i));
        }

        return coordinates;
    }
}
