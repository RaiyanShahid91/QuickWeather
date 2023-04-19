package com.quick.quickweather.config;


import com.quick.quickweather.fragment.model.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<Example> getWeather(
            @Query("q")
            String city,
            @Query("appid")
            String apiKey);

    @GET("forecast")
    Call<Example> getForecastWeather(
            @Query("q")
            String city,
            @Query("appid")
            String apiKey);


}
