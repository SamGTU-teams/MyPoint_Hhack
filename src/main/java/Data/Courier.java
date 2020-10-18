package Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Courier extends Personality<Courier> {

    private List<Object> pars;
    {
        pars = new ArrayList<>();
    }

    public Courier(Point location, long id) {
        super(location, id);
    }

    @Override
    public Courier initTrial(Trial trial) {
        this.trial = trial;
        pars.add("origin=" + location.lat + "%2C" + location.lon + trial);
        return this;
    }

    @Override
    public Stream<String> parameters() {
        return pars.stream().map(Object::toString);
    }

    public void updatePosition(Point location) {
        this.location = location;
    }

    public Courier rideMode(String rideMode) {
        this.rideMode = rideMode;
        pars.add("mode=" + rideMode);
        return this;
    }
}
