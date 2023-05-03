package ee.daniel.datanor_test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class WeatherControllerTests {

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private WeatherController weatherController;

    @Test
    public void testGetWeatherData() {
        ResponseEntity<String> response = weatherController.getWeatherData("New York");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\": \"Weather data saved successfully.\"}", response.getBody());
        List<WeatherData> weatherData = weatherRepository.findAllByCity("New York");
        assertEquals(1, weatherData.size());
        WeatherData savedWeatherData = weatherData.get(0);
        assertEquals("New York", savedWeatherData.getCity());
        assertNotNull(savedWeatherData.getTimestamp());
    }

    @Test
    public void testGetWeatherDataHistory() {
        weatherRepository.save(new WeatherData("Tartu", 10.0, 10.0, 10.0, LocalDateTime.now()));
        weatherRepository.save(new WeatherData("Tartu", 15.0, 15.0, 15.0, LocalDateTime.now()));
        ResponseEntity<List<WeatherData>> response = weatherController.getWeatherDataHistory("Tartu");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void testDeleteWeatherData() {
        weatherRepository.save(new WeatherData("New York", 10.0, 10.0, 10.0, LocalDateTime.now()));
        ResponseEntity<String> response = weatherController.deleteWeatherData("New York");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\": \"Weather data for New York deleted successfully.\"}", response.getBody());
        List<WeatherData> weatherData = weatherRepository.findAllByCity("New York");
        assertEquals(0, weatherData.size());
    }

    @Test
    public void testGetAllCities() {
        weatherRepository.save(new WeatherData("New York", 10.0, 10.0, 10.0, LocalDateTime.now()));
        weatherRepository.save(new WeatherData("Los Angeles", 15.0, 15.0, 15.0, LocalDateTime.now()));
        ResponseEntity<List<String>> response = weatherController.getAllCities();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("New York"));
        assertTrue(response.getBody().contains("Los Angeles"));
        assertEquals(2, response.getBody().size());
    }
}
