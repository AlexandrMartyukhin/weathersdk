package ru.minilan.weatherlib;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.minilan.weatherlib.retrofit.WeatherRequestInterface;
import ru.minilan.weatherlib.retrofit.model.City;
import ru.minilan.weatherlib.retrofit.model.ConsolidatedWeather;
import ru.minilan.weatherlib.retrofit.model.WeatherEntity;

public class Weather {

    private WeatherRequestInterface weatherRequestInterface;
    private final Handler handler;
    private static final String BASE_URL = "https://www.metaweather.com/api/";
    private OnGetWeatherCallback getWeatherCallback;

    public Weather() {
        handler = new Handler(Looper.getMainLooper());
        initRetrofit();
    }

    public void getCurrentWeather(String location, OnGetWeatherCallback getWeatherCallback) {
        this.getWeatherCallback = getWeatherCallback;

        Executors.newFixedThreadPool(1).execute(() -> {

            if (location != null && !location.isEmpty()) {

                try {

                    // находимся не в главном потоке, поэтому запустим синхронно
                    Response<List<City>> responseCity = weatherRequestInterface.searchCity(location).execute();
                    if (responseCity != null && responseCity.body() != null) {

                        //можно передавать обратно список для выбора, но в задании такого нет - выберем первый элемент
                        List<City> cityList = responseCity.body();
                        if (cityList.size() > 0) {
                            int woeid = cityList.get(0).getWoeid();

                            Response<WeatherEntity> response = weatherRequestInterface.loadWeather(String.valueOf(woeid)).execute();
                            if (response != null && response.body() != null) {

                                WeatherEntity weatherEntity = response.body();

                                // получим погоду на текущий день из списка в 5 дней
                                ConsolidatedWeather weather = weatherEntity.getConsolidatedWeather().get(0);

                                //можно отдавать из библиотеки объектом, но поскольку нет задания отдавать целый объект под разбор в клиенте,
                                //то вернем строкой произвольную часть данных

                                @SuppressLint("DefaultLocale") String stringAnswer =
                                        "City: " + weatherEntity.getTitle() + System.lineSeparator() +
                                        "Date: " + weather.getApplicableDate() + System.lineSeparator() +
                                        "Temp: " + String.format("%.1f", weather.getTheTemp()) + System.lineSeparator() +
                                        "Humidity: " + weather.getHumidity() + System.lineSeparator() +
                                        "State: " + weather.getWeatherStateName() + System.lineSeparator();

                                onSuccess(stringAnswer);
                            }

                        } else {
                            onError(new Exception("No location found"));
                        }

                    } else {
                        onError(new Exception("No location found"));
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            } else {
                onError(new Exception("Error! Location is empty"));
            }
        });
    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherRequestInterface = retrofit.create(WeatherRequestInterface.class);
    }

    private void onError(Exception exception) {
        if (getWeatherCallback != null) {
            handler.post(() -> getWeatherCallback.onError(exception));
        }
    }

    private void onSuccess(String answer) {
        if (getWeatherCallback != null) {
            handler.post(() -> getWeatherCallback.onSuccess(answer));
        }
    }


}
