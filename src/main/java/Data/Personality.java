package Data;

import java.util.stream.Stream;

public abstract class Personality<T extends Personality<T>> {
    String rideMode;
    Point location;
    long id;
    Trial trial;

    public Personality(Point location, long id) {
        this.location = location;
        this.id = id;
    }

    public abstract T initTrial(Trial value);

    /**
     * @return a stream with all parameters from <T extends Personality<T>> class for URL Request
     */
    public abstract Stream<String> parameters();

}
