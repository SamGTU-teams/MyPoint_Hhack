package Data;

import Exception.GoogleRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Trial {
    private Point startPoint;
    private final List<Point> intermediate;

    public Trial(Point startPoint) {
        this.startPoint = startPoint;
        intermediate = new ArrayList<>();
    }

    public int interSize() {
        return intermediate.size();
    }

    /**
     * @param places add way points to Trial(array count can't be more than 25)
     * @return the size of all added places
     */
    public int addWayPoint(Point... places) {
        if (intermediate.size() + places.length > 25)
            throw new GoogleRequestException("No more 25 waypoints must be in query");
        intermediate.addAll(Arrays.asList(places));
        return intermediate.size();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("&destination=").append(startPoint).append("&language=ru");
        if (intermediate.size() != 0) {
            String ways = intermediate.stream().map(Point::toString).collect(Collectors.joining("%7C", "via:", ""));
            str.append("&waypoints=").append(ways);
        }
        return str.toString();
    }
}
