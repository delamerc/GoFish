package com.example.leo.gofish;

/**
 * Created by Leo on 2016-11-19.
 */

public interface AsyncWeatherResponse {
    void onTaskComplete(Weather weather);
}
