package ee.daniel.datanor_test;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Configuration
@EnableJpaRepositories
@EntityScan
public class DatanorTestApplication {

	public DatanorTestApplication(WeatherScheduler weatherScheduler) {
		this.weatherScheduler = weatherScheduler;
	}

	public static void main(String[] args) {
		SpringApplication.run(DatanorTestApplication.class, args);
	}

	private final WeatherScheduler weatherScheduler;


	@PostConstruct
	public void init() {
		weatherScheduler.scheduleWeatherChecks();
	}

}
