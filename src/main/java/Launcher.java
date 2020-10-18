import GoogleAPI.DirectionCreator;
import GoogleAPI.GeoCoding;
import DataBaseQueries.DBListener;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Launcher {

    private static String URL = "jdbc:mysql://37.140.192.78:3306/u1063682_sqlmipoint";
    private static String pathProperties = "src\\main\\resources\\";

    public static void main(String[] args) {
        Properties SQLProperties = new Properties();
        Properties TelegramProperties = new Properties();
        Properties GoogleProperties = new Properties();
        try (FileReader SQLReader = new FileReader(pathProperties + "connectionToDB.properties");
             FileReader TelegramReader = new FileReader(pathProperties + "Telegram.properties");
             FileReader GoogleReader = new FileReader(pathProperties + "Google.properties")) {
            SQLProperties.load(SQLReader);
            TelegramProperties.load(TelegramReader);
            GoogleProperties.load(GoogleReader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GeoCoding.setApiKey(GoogleProperties);
        DirectionCreator.setApiKey(GoogleProperties);

        DBListener listener = new DBListener(URL, SQLProperties);
        listener.run();

    }
}
