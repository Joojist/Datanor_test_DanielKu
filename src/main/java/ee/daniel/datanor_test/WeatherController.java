package ee.daniel.datanor_test;

import org.json.JSONObject;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@EnableAutoConfiguration
@Component
public class WeatherController {
    final
    WeatherRepository weatherRepository;

    public WeatherController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    //add a city
    @GetMapping("/weather/{city}")
    public ResponseEntity<String> getWeatherData(@PathVariable String city) {
        String weatherData = OpenWeatherMapAPI.getWeatherData(city);
        if (weatherData == null) {
            return new ResponseEntity<>("{\"error\": \"Unable to fetch weather data.\"}", HttpStatus.BAD_REQUEST);
        }
        JSONObject json = new JSONObject(weatherData);
        double temperatureInKelvin = json.getJSONObject("main").getDouble("temp");
        Double temperatureInCelsius = temperatureInKelvin - 273.15; // Kelvin to Celsius
        DecimalFormat df = new DecimalFormat("#.#");
        Double formattedTemperature = Double.valueOf(df.format(temperatureInCelsius));
        Double humidity = json.getJSONObject("main").getDouble("humidity");
        Double windSpeed = json.getJSONObject("wind").getDouble("speed");
        String formattedCity = formatCityName(city);
        WeatherData weather = new WeatherData(formattedCity, formattedTemperature, windSpeed, humidity, LocalDateTime.now());
        weatherRepository.save(weather);
        return new ResponseEntity<>("{\"message\": \"Weather data saved successfully.\"}", HttpStatus.OK);
    }
    //check city data
    @GetMapping("/weather/{city}/history")
    public ResponseEntity<List<WeatherData>> getWeatherDataHistory(@PathVariable String city) {
        String formattedCity = formatCityName(city);
        List<WeatherData> weatherData = weatherRepository.findAllByCity(formattedCity);
        if (weatherData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(weatherData, HttpStatus.OK);
    }

    //delete city
    @DeleteMapping("/deleteweather/{city}")
    public ResponseEntity<String> deleteWeatherData(@PathVariable String city) {
        List<WeatherData> weatherDataList = weatherRepository.findByCity(city);
        if (weatherDataList.isEmpty()) {
            return new ResponseEntity<>("{\"error\": \"Weather data for " + city + " not found.\"}", HttpStatus.NOT_FOUND);
        }
        weatherRepository.deleteAll(weatherDataList);
        return new ResponseEntity<>("{\"message\": \"Weather data for " + city + " deleted successfully.\"}", HttpStatus.OK);
    }

    //list cities
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        List<String> cities = weatherRepository.findAllCities();
        if (cities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }
    //linna nimi korda
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
