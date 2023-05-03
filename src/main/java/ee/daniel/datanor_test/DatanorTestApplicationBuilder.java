package ee.daniel.datanor_test;

public class DatanorTestApplicationBuilder {
    private WeatherScheduler weatherScheduler;

    public DatanorTestApplicationBuilder setWeatherScheduler(WeatherScheduler weatherScheduler) {
        this.weatherScheduler = weatherScheduler;
        return this;
    }

    public DatanorTestApplication createDatanorTestApplication() {
        return new DatanorTestApplication(weatherScheduler);
    }
}