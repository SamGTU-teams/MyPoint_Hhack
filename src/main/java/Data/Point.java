package Data;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

public class Point {
    public double latitude;
    public double longitude;

    public transient final static JsonDeserializer<Point> pointJsonDeserializer = (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject firstEl = jsonElement.getAsJsonObject().getAsJsonArray("results").get(0).
                getAsJsonObject();
        JsonObject location = firstEl.getAsJsonObject("geometry").getAsJsonObject("location");
        return new Point(location.getAsJsonPrimitive("lat").getAsDouble(),
                location.getAsJsonPrimitive("lng").getAsDouble());
    };

    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("%f%s%f", latitude, "%2C", longitude).replaceAll(",", ".");
    }
}
