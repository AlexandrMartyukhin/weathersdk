package ru.minilan.weatherlib;

public interface OnGetWeatherCallback {
    void onSuccess(String data);
    void onError(Exception exception);
}
