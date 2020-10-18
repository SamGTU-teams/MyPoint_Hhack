package GoogleAPI;

import Data.Point;
import Exception.GoogleRequestException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class GeoCoding {
    private transient static final String GEO_REQ = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private transient static String API_KEY = null;

    /**
     * From place name to coordinates
     **/
    public static Point geoCodePoint(String placeName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Point.class, Point.pointJsonDeserializer).create();
        return gson.fromJson(Objects.requireNonNull(readJson(placeName)).orElseThrow(GoogleRequestException::new),
                Point.class);
    }

    public static void setApiKey(Properties info) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            API_KEY = info.getProperty("key");
        }
    }

    private static Optional<Reader> readJson(String placeName) {
        try {
            HttpURLConnection http = (HttpURLConnection) new URL(String.format("%s%s&language=ru&key=%s", GEO_REQ,
                    placeName.replaceAll(",|\\.|\\s", "+"), API_KEY)).openConnection();
            http.disconnect();
            return Optional.of(new InputStreamReader(http.getInputStream()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
