package com.example.leo.gofish;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;

/**
 * Created by Leo on 2016-11-19.
 */

public class WeatherFragment extends Fragment {

    private TextView mSummary, mTemp, mFeel, mWind, mPressure;
    private ImageView mIcon;
    private Station mStation;
    private Weather mWeather;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mStation = (Station) bundle.getSerializable("Station");
        mWeather = mStation.getWeather();

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
        initViews();
        setInformation();
    }

    private void setInformation() {
        mSummary.setText(mWeather.getSummary());
        setImage(mWeather.getIcon());
        mTemp.setText(mWeather.temperatureToString());
        mFeel.setText(" " + mWeather.apparentTempToString());
        mWind.setText(" " + mWeather.windToString());
        mPressure.setText(" " + mWeather.pressureToString());
    }

    private void setImage(String img) {
        Resources res = getActivity().getResources();
        String mDrawableName = img.replaceAll("-","");
        int resID = res.getIdentifier(mDrawableName, "mipmap", getActivity().getPackageName());
        Drawable drawable = res.getDrawable(resID);
        mIcon.setImageDrawable(drawable);
    }

    private void initViews() {
        mSummary = (TextView) getView().findViewById(R.id.summary);
        mIcon = (ImageView) getView().findViewById(R.id.icon);
        mTemp = (TextView) getView().findViewById(R.id.temperature);
        mFeel = (TextView) getView().findViewById(R.id.apparentTemperature);
        mWind = (TextView) getView().findViewById(R.id.windSpeed);
        mPressure = (TextView) getView().findViewById(R.id.pressure);
    }
}
