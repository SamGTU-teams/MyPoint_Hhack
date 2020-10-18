package GoogleAPI;

import Data.Personality;
import Data.ResultTrial;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.stream.Collectors;

public class DirectionCreator {
    private static String API_KEY = null;
    static final String BEGIN_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    public static String createParameters(Personality<? extends Personality> c) {
        return c.parameters().collect(Collectors.joining("&", BEGIN_URL, API_KEY));
    }

    public static ResultTrial getResult(Personality<? extends Personality> c) throws IOException {
        return getResult(createParameters(c));
    }

    public static ResultTrial getResult(String query)  throws IOException {
        HttpURLConnection http = (HttpURLConnection) new URL(query).openConnection();
        Reader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
        Gson gson = new GsonBuilder().registerTypeAdapter(ResultTrial.class, ResultTrial.resTrialJsonDeser).create();
        ResultTrial resTrial = gson.fromJson(reader, ResultTrial.class);
        http.disconnect();
        return resTrial;
    }

    public static void setApiKey(Properties info){
        if(API_KEY == null || API_KEY.isEmpty()){
            API_KEY = info.getProperty("key");
        }
    }
}
