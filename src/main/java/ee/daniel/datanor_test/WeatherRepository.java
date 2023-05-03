package ee.daniel.datanor_test;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Hidden
@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    @Query(value = "SELECT DISTINCT w.city FROM WeatherData w")
    List<String> findAllCities();

    List<WeatherData> findAllByCity(@Param("formattedCity") String formattedCity);

    List<WeatherData> findByCity(String city);
}
