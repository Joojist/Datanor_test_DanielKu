package ee.daniel.datanor_test;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Component

public class WeatherScheduler {

    private final WeatherRepository weatherRepository;
    private final Set<String> cities;

    public WeatherScheduler(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
        this.cities = new HashSet<>();
    }

    public void scheduleWeatherChecks() {
        List<String> allCities = weatherRepository.findAllCities();
        for (String city : allCities) {
            String formattedCity = formatCityName(city);
            if (!cities.contains(formattedCity)) {
                cities.add(formattedCity);
            }
        }

        //weather checks every 15 minutes
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::performWeatherChecks, 0, 15, TimeUnit.MINUTES);
    }

    private void performWeatherChecks() {
        // all unique + new cities
        List<String> allCities = weatherRepository.findAllCities();
        boolean newCityAdded = false;
        for (String city : allCities) {
            String formattedCity = formatCityName(city);
            if (!cities.contains(formattedCity)) {
                cities.add(formattedCity);
                newCityAdded = true;
                checkWeather(formattedCity);
            }
        }
        if (!newCityAdded) { // perform checkup for all unique cities
            for (String city : allCities) {
                String formattedCity = formatCityName(city);
                if (!cities.contains(formattedCity)) {
                    cities.add(formattedCity);
                }
                checkWeather(formattedCity);
            }
        }
    }

    private void checkWeather(String city) {
        String weatherData = OpenWeatherMapAPI.getWeatherData(city);
        if (weatherData == null) {
            return;
        }
        JSONObject json = new JSONObject(weatherData);
        double temperatureInKelvin = json.getJSONObject("main").getDouble("temp");
        Double temperatureInCelsius = temperatureInKelvin - 273.15; // kelvin to celsius
        DecimalFormat df = new DecimalFormat("#.#");
        Double formattedTemperature = Double.valueOf(df.format(temperatureInCelsius));
        Double humidity = json.getJSONObject("main").getDouble("humidity");
        Double windSpeed = json.getJSONObject("wind").getDouble("speed");
        WeatherData weather = new WeatherData(city, formattedTemperature, windSpeed, humidity, LocalDateTime.now());
        weatherRepository.save(weather);
    }

    private String formatCityName(String cityName) {
        String[] words = cityName.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                sb.append(word.substring(1).toLowerCase());
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}