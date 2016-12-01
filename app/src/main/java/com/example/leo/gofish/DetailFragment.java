package com.example.leo.gofish;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Karlo on 11/19/2016.
 */

public class DetailFragment extends Fragment implements AsyncDLResponse {
    private Station station;
    private TextView mStationId;
    private TextView mStationName;
    private TextView mStationProvince;
    private TextView mStationLong;
    private TextView mStationLat;
    private TextView mStationWaterLevel;
    private TextView mStationDischarge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        station = (Station) bundle.getSerializable("Station");

        DownloadFile df = new DownloadFile(getActivity());
        if (!(df.getStatus() == AsyncTask.Status.RUNNING)) {
            df.execute(station);
            df.delegate = this;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onTaskComplete(Station s) {
        init();
        mStationId.setText(" " + station.getId());
        mStationName.setText(" " + station.getName());
        mStationProvince.setText(" " + station.getProvince());
        mStationLat.setText(" " + Double.toString(station.getLatitude()));
        mStationLong.setText(" " + Double.toString(station.getLongitude()));
        mStationWaterLevel.setText(" " + Double.toString(station.getWaterLevel()));
        mStationDischarge.setText(" " + Double.toString(station.getDischarge()));
    }

    public void init() {
        try {
            InputStream inputstream = new FileInputStream(getActivity().getFilesDir() + "/stations/hourly/" + station.getFileName());
            CSVFile csv = new CSVFile(inputstream);
            station = csv.readStation(station);

            WeatherFragment weatherFrag = new WeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Station", station);
            weatherFrag.setArguments(bundle);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.child_weather_fragment, weatherFrag).commit();
        } catch (IOException e) {
            Log.i("Detail Fragment: ", "File not found");
        }
    }

    public void initView() {
        mStationId = (TextView) getView().findViewById(R.id.station_id);
        mStationName = (TextView) getView().findViewById(R.id.station_name);
        mStationProvince = (TextView) getView().findViewById(R.id.station_province);
        mStationLat = (TextView) getView().findViewById(R.id.station_lat);
        mStationLong = (TextView) getView().findViewById(R.id.station_long);
        mStationWaterLevel = (TextView) getView().findViewById(R.id.station_waterLevel);
        mStationDischarge = (TextView) getView().findViewById(R.id.station_discharge);
    }

    private Location createNewLocation(double longitude, double latitude) {
        Location location = new Location("weatherprovider");
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }
}
