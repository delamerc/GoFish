package com.example.leo.gofish;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karlo on 11/19/2016.
 */

public class DetailFragment extends Fragment implements AsyncDLResponse, OnMapReadyCallback {
    private CheckBox mCheckBox;
    private Station mStation;
    private TextView mWaterLevel, mDischarge;
    private View mChart;

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

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.detail_map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.detail_map, fragment).commit();
        }
        fragment.getMapAsync(this);

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
    }
    //=============================================================================//
    //---------------------------------View inits-----------------------------------------//
    //=============================================================================//
    public void initView() {
        mCheckBox = (CheckBox) getView().findViewById(R.id.detail_checkbox);
        mWaterLevel = (TextView)getView().findViewById(R.id.current_waterlevel);
        mDischarge= (TextView)getView().findViewById(R.id.current_discharge);
    }
    public void init() {
        try {
            InputStream inputstream = new FileInputStream(getActivity().getFilesDir() + "/stations/hourly/" + mStation.getFileName());
            CSVFile csv = new CSVFile(inputstream);
            mStation = csv.readStation(mStation);
            mWaterLevel.setText(String.valueOf(mStation.getWaterLevel())+ " cm/s");
            mDischarge.setText(String.valueOf(mStation.getDischarge())+ " cm/s" );
            initChart(mStation.getStationHistory());

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

    //=============================================================================//
    //---------------------------------Map-----------------------------------------//
    //=============================================================================//
    private SupportMapFragment fragment;
    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latlang = new LatLng(mStation.getLatitude(), mStation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latlang).title(mStation.getName()));

        CameraUpdate center = CameraUpdateFactory.newLatLng(latlang);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(9);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    //=============================================================================//
    //---------------------------------Favorites -----------------------------------------//
    //=============================================================================//

    private void addToFavourites(Station s) {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        databaseConnector.insertStation(s.getId(), s.getName(), s.getProvince());
    }

    private void removeFromFavourites(Station s) {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        databaseConnector.deleteStation(s.getId());
    }

    //=============================================================================//
    //---------------------------------Chart -----------------------------------------//
    //=============================================================================//


    private void initChart(StationHistory sh) {
        List<Double> dc = sh.getDisharges();
        List<Double> wl = sh.getWaterLevels();
        ArrayList<String> dates = sh.getDates();

        XYSeries discharge = new XYSeries("Discharge");
        XYSeries waterLevel = new XYSeries("Water Level");

        for (int i = 0; i < dc.size(); i++) {
            discharge.add(i, dc.get(i));
            waterLevel.add(i, wl.get(i));
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        dataset.addSeries(discharge);
        dataset.addSeries(waterLevel);

        XYSeriesRenderer dischargeRenderer = new XYSeriesRenderer();
        dischargeRenderer.setShowLegendItem(true);
        dischargeRenderer.setColor(Color.RED);
        dischargeRenderer.setFillPoints(true);
        dischargeRenderer.setLineWidth(2f);
        dischargeRenderer.setDisplayChartValues(true);
        dischargeRenderer.setChartValuesTextSize(15);
        dischargeRenderer.setDisplayChartValuesDistance(10);
        dischargeRenderer.setPointStyle(PointStyle.CIRCLE);
        dischargeRenderer.setStroke(BasicStroke.SOLID);

        XYSeriesRenderer watlevRenderer = new XYSeriesRenderer();
        watlevRenderer.setShowLegendItem(true);
        watlevRenderer.setColor(Color.BLUE);
        watlevRenderer.setFillPoints(true);
        watlevRenderer.setLineWidth(2f);
        watlevRenderer.setDisplayChartValues(true);
        watlevRenderer.setChartValuesTextSize(15);
        watlevRenderer.setPointStyle(PointStyle.SQUARE);
        watlevRenderer.setStroke(BasicStroke.SOLID);

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(10);
        multiRenderer.setChartTitle("Water Condition Data");
        multiRenderer.setYTitle("cm/s");
        multiRenderer.setChartTitleTextSize(20);
        multiRenderer.setAxisTitleTextSize(15);
        multiRenderer.setLegendTextSize(20);
        multiRenderer.setLabelsTextSize(15);

        multiRenderer.setZoomButtonsVisible(false);
        multiRenderer.setPanEnabled(true, false);

        multiRenderer.setClickEnabled(false);
        multiRenderer.setZoomEnabled(true, false);
        multiRenderer.setShowGridY(true);
        multiRenderer.setShowGridX(true);
        multiRenderer.setFitLegend(true);
        multiRenderer.setShowGrid(true);
        multiRenderer.setExternalZoomEnabled(false);
        multiRenderer.setAntialiasing(true);
        multiRenderer.setInScroll(false);

        multiRenderer.setLegendHeight(100);
        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);
        multiRenderer.setXLabelsPadding(10);
        multiRenderer.setXLabelsAngle(45);
        multiRenderer.setBarSpacing(2);

        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        multiRenderer.setYLabels(10);

        multiRenderer.setXAxisMin(-0.5);
        multiRenderer.setXAxisMax(10);

        multiRenderer.setApplyBackgroundColor(false);
        multiRenderer.setScale(4f);
        multiRenderer.setPointSize(2f);

        for (int i = 0; i < dates.size(); i++) {
            multiRenderer.addXTextLabel(i, dates.get(i).substring(5, 10) + dates.get(i).substring(13, 16));
        }

        multiRenderer.addSeriesRenderer(dischargeRenderer);
        multiRenderer.addSeriesRenderer(watlevRenderer);

        LinearLayout chartContainer = (LinearLayout) getActivity().findViewById(R.id.chart);
        chartContainer.removeAllViews();

        mChart = ChartFactory.getLineChartView(getActivity(), dataset, multiRenderer);

        chartContainer.addView(mChart);
    }
}


