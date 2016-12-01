package com.example.leo.gofish;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Leo on 2016-11-19.
 */

public class WeatherFragment extends Fragment implements AsyncWeatherResponse {

    TextView summary, icon,lat ,lng,temp,feel,wind,windDir,pressure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment, container, false);

        Location loc = createNewLocation(44.041870,-79.502407);


        ForecastIO forecastIO = new ForecastIO();
        forecastIO.delegate=this;
        forecastIO.execute(loc);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        summary = (TextView)getView().findViewById(R.id.summary);
        //icon = (TextView)getView().findViewById(R.id.icon);
        //lat = (TextView)getView().findViewById(R.id.latitude);
        //lng = (TextView)getView().findViewById(R.id.longitude);
        temp = (TextView)getView().findViewById(R.id.temperature);
        feel = (TextView)getView().findViewById(R.id.apparentTemperature);
        wind = (TextView)getView().findViewById(R.id.windSpeed);
        //windDir = (TextView)getView().findViewById(R.id.windBearing);
        pressure = (TextView)getView().findViewById(R.id.pressure);


    }

    Location createNewLocation(double longitude, double latitude) {
        Location location = new Location("dummyprovider");
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }

    @Override
    public void onTaskComplete(Weather weather) {
        summary.setText(weather.getSummary());
        // icon.setText(weather.getIcon());
        //lat.setText(Double.toString(weather.getLatitude()));
        //lng.setText(Double.toString(weather.getLongitude()));
        temp.setText(Double.toString(weather.getTemperature()));
        feel.setText(Double.toString(weather.getApparentTemperature()));
        wind.setText(Double.toString(weather.getWindSpeed()));
        // windDir.setText(weather.getWindBearing());
        pressure.setText(Double.toString(weather.getPressure()));
    }
}
