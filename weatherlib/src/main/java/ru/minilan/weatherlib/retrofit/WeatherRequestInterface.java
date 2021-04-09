package ru.minilan.weatherlib.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.minilan.weatherlib.retrofit.model.City;
import ru.minilan.weatherlib.retrofit.model.WeatherEntity;

public interface WeatherRequestInterface {
    @GET("location/search")
    Call<List<City>> searchCity(@Query("query") String location);

    @GET("location/{woeid}")
    Call<WeatherEntity> loadWeather(@Path("woeid") String woeid);
}
