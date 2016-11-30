package com.example.leo.gofish;

import android.os.AsyncTask;
import android.os.Bundle;
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
    private TextView mStationgWaterLevel;
    private TextView mStationDischarge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        station  = (Station) bundle.getSerializable("Station");

        DownloadFile df = new DownloadFile(getActivity());
        if(!(df.getStatus() == AsyncTask.Status.RUNNING)) {
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
    public void onTaskComplete(Station s) {
        //station.setFileName(s.getFileName());
        init();

        mStationId = (TextView) getView().findViewById(R.id.station_id);
        mStationId.setText("Station Id: " + station.getId());

        mStationName = (TextView) getView().findViewById(R.id.station_name);
        mStationName.setText("Station Name: " + station.getName());

        mStationProvince = (TextView) getView().findViewById(R.id.station_province);
        mStationProvince.setText("Province: " + station.getProvince());

        mStationLat = (TextView) getView().findViewById(R.id.station_lat);
        mStationLat.setText("Latitude: " + Double.toString(station.getLatitude()));

        mStationLong = (TextView) getView().findViewById(R.id.station_long);
        mStationLong.setText("Longitude: " + Double.toString(station.getLongitude()));

        mStationgWaterLevel = (TextView) getView().findViewById(R.id.station_waterlevel);
        mStationgWaterLevel.setText("Water Level: " + Double.toString(station.getWaterLevel()));

        mStationDischarge = (TextView) getView().findViewById(R.id.station_discharge);
        mStationDischarge.setText("Discharge: " + Double.toString(station.getDischarge()));
    }

    public void init() {
        try {
            InputStream inputstream = new FileInputStream(getActivity().getFilesDir() + "/stations/hourly/" + station.getFileName());
            CSVFile csv = new CSVFile(inputstream);
            station = csv.readStation(station);
        } catch (IOException e) {
            Log.i("Detail Fragment: ", "File not found");
        }
    }
}
