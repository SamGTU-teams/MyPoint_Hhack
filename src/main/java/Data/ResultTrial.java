package Data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ResultTrial {
    private String endAddress, startAddress;
    private Distance distance;
    private Duration duration;

    public transient static final JsonDeserializer<ResultTrial> resTrialJsonDeser = (jsonElement, type, context) -> {
        JsonArray legs = jsonElement.getAsJsonObject().getAsJsonArray("routes").
                get(0).getAsJsonObject().getAsJsonArray("legs").getAsJsonArray();
        ResultTrial rT = new ResultTrial();
        JsonObject firstEl = legs.get(0).getAsJsonObject();
        rT.duration = context.deserialize(firstEl.get("duration"), Duration.class);
        rT.distance = context.deserialize(firstEl.get("distance"), Distance.class);
        rT.startAddress = firstEl.getAsJsonPrimitive("start_address").getAsString();
        rT.endAddress = firstEl.getAsJsonPrimitive("end_address").getAsString();
        return rT;
    };

    public String getEndAddress() {
        return endAddress;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "ResultTrial{" +
                "endAddress='" + endAddress + '\'' +
                ", startAddress='" + startAddress + '\'' +
                ", distance=" + distance +
                ", duration=" + duration +
                '}';
    }

    public static class Distance{
        @SerializedName("text")
        private String textDistance;
        @SerializedName("value")
        private long metres;

        public String getTextDistance() {
            return textDistance;
        }

        public long getMetres() {
            return metres;
        }

        @Override
        public String toString() {
            return "Distance{" +
                    "textDistance='" + textDistance + '\'' +
                    ", metres=" + metres +
                    '}';
        }
    }
    public static class Duration{
        @SerializedName("text")
        private String textTime;
        @SerializedName("value")
        private long seconds;

        public String getTextTime() {
            return textTime;
        }

        public long getSeconds() {
            return seconds;
        }

        @Override
        public String toString() {
            return "Duration{" +
                    "textTime='" + textTime + '\'' +
                    ", seconds=" + seconds +
                    '}';
        }
    }
}
