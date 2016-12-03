package com.example.leo.gofish;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Karlo on 11/19/2016.
 */

public class DetailFragment extends Fragment implements AsyncDLResponse {
    private Station mStation;
    private TextView mStationId;
    private TextView mStationName;
    private TextView mStationProvince;
    private TextView mStationLong;
    private TextView mStationLat;
    private TextView mStationWaterLevel;
    private TextView mStationDischarge;
    private CheckBox mCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mStation = (Station) bundle.getSerializable("Station");

        DownloadFile df = new DownloadFile(getActivity());
        if (!(df.getStatus() == AsyncTask.Status.RUNNING)) {
            df.execute(mStation);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                if (cb.isChecked()) {
                    mStation.setFavourite(true);
                    AsyncTask<Station, Object, Object> addFavouriteTask = new AsyncTask<Station, Object, Object>() {

                        @Override
                        protected Object doInBackground(Station... objects) {
                            Station s = objects[0];
                            addToFavourites(s);
                            return null;
                        }
                    };

                    addFavouriteTask.execute(mStation);
                } else {
                    mStation.setFavourite(false);
                    AsyncTask<Station, Object, Object> removeFavouriteTask = new AsyncTask<Station, Object, Object>() {

                        @Override
                        protected Object doInBackground(Station... objects) {
                            Station s = objects[0];
                            removeFromFavourites(s);
                            return null;
                        }
                    };
                    removeFavouriteTask.execute(mStation);
                }
            }
        });
    }

    @Override
    public void onTaskComplete(Station s) {
        init();
        mStationId.setText(" " + mStation.getId());
        mStationName.setText(" " + mStation.getName());
        mStationProvince.setText(" " + mStation.getProvince());
        mStationLat.setText(" " + Double.toString(mStation.getLatitude()));
        mStationLong.setText(" " + Double.toString(mStation.getLongitude()));
        mStationWaterLevel.setText(" " + Double.toString(mStation.getWaterLevel()));
        mStationDischarge.setText(" " + Double.toString(mStation.getDischarge()));
        mCheckBox.setChecked(mStation.isFavourite());
    }

    public void init() {
        try {
            InputStream inputstream = new FileInputStream(getActivity().getFilesDir() + "/stations/hourly/" + mStation.getFileName());
            CSVFile csv = new CSVFile(inputstream);
            mStation = csv.readStation(mStation);

            WeatherFragment weatherFrag = new WeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Station", mStation);
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
        mCheckBox = (CheckBox) getView().findViewById(R.id.detail_checkbox);
    }


    private void addToFavourites(Station s) {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        databaseConnector.insertStation(s.getId(), s.getName(), s.getProvince());
    }

    private void removeFromFavourites(Station s) {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        databaseConnector.deleteStation(s.getId());
    }
}
