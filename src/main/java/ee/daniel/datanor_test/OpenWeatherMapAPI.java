package ee.daniel.datanor_test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OpenWeatherMapAPI {

    private static final String API_KEY = "93295783f42aef38e4dfe08121e4036b";

    public static String getWeatherData(String city) {
        try {
            URL url = new URL(
                    "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return null;
            }
            Scanner scanner = new Scanner(url.openStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
