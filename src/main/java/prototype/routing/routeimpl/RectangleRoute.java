package prototype.routing.routeimpl;

import prototype.model.Measurement;
import prototype.routing.IRoute;

import java.util.ArrayList;
import java.util.List;

public class RectangleRoute implements IRoute {
    @Override
    public List<Measurement> getRouteAsList() {
        ArrayList<Measurement> measurements = new ArrayList<>();
        int maxSize = 10;

        for (int i = 0; i < maxSize; i++) {
            measurements.add(new Measurement(i, 0));
        }
        for (int i = 0; i < maxSize; i++) {
            measurements.add(new Measurement(maxSize - 1, i));
        }
        for (int i = maxSize - 1; i >= 0; i--) {
            measurements.add(new Measurement(i, maxSize - 1));
        }
        for (int i = maxSize - 1; i >= 0; i--) {
            measurements.add(new Measurement(0, i));
        }

        return measurements;
    }
}
