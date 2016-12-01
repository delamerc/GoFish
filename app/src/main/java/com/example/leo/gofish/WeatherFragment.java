package com.example.leo.gofish;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;

/**
 * Created by Leo on 2016-11-19.
 */

public class WeatherFragment extends Fragment {

    TextView summary, temp, feel, wind, pressure;
    ImageView icon;
    private Station station;
    private Weather weather;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        station = (Station) bundle.getSerializable("Station");
        weather = station.getWeather();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        summary = (TextView) getView().findViewById(R.id.summary);
        icon = (ImageView) getView().findViewById(R.id.icon);
        temp = (TextView) getView().findViewById(R.id.temperature);
        feel = (TextView) getView().findViewById(R.id.apparentTemperature);
        wind = (TextView) getView().findViewById(R.id.windSpeed);
        pressure = (TextView) getView().findViewById(R.id.pressure);

        summary.setText(weather.getSummary());
        setImage(weather.getIcon());
        temp.setText(weather.temperatureToString());
        feel.setText(" " + weather.apparentTempToString());
        wind.setText(" " + weather.windToString());
        pressure.setText(" " + weather.pressureToString());
    }

    private void setImage(String img) {
        Resources res = getActivity().getResources();
        String mDrawableName = img.replaceAll("-","");
        int resID = res.getIdentifier(mDrawableName, "mipmap", getActivity().getPackageName());
        Drawable drawable = res.getDrawable(resID);
        icon.setImageDrawable(drawable);
    }
}
