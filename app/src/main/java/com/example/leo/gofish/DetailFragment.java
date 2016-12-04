package com.example.leo.gofish;

import android.graphics.Color;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import im.dacer.androidcharts.LineView;

/**
 * Created by Karlo on 11/19/2016.
 */

public class DetailFragment extends Fragment implements AsyncDLResponse, OnMapReadyCallback {
    private Station station;
    private LineView lineView;
    GraphView graph;
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

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.detail_map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.detail_map, fragment).commit();
        }
        fragment.getMapAsync(this);

        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        lineView = (LineView) view.findViewById(R.id.line_view);
        graph = (GraphView) view.findViewById(R.id.graph);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onTaskComplete(Station s) {
        init();
    }

    public void init() {
        try {
            InputStream inputstream = new FileInputStream(getActivity().getFilesDir() + "/stations/hourly/" + station.getFileName());
            CSVFile csv = new CSVFile(inputstream);
            station = csv.readStation(station);

            initChart(station.getStationHistory());
            initChart2(station.getStationHistory());
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

    //=============================================================================//
    //---------------------------------Map-----------------------------------------//
    //=============================================================================//
    private SupportMapFragment fragment;
    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latlang = new LatLng(station.getLatitude(), station.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latlang).title(station.getName()));

        CameraUpdate center = CameraUpdateFactory.newLatLng(latlang);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(9);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    //=============================================================================//
    //---------------------------------Chart 2-----------------------------------------//
    //=============================================================================//

    private void initChart(StationHistory sh) {
        int randomint = 9;
        lineView.setBottomTextList(parseDates(sh.getDates()));
        lineView.setColorArray(new int[]{Color.parseColor("#0066ff"),Color.parseColor("#F44336"),Color.parseColor("#2196F3"),Color.parseColor("#009688")});
        lineView.setDrawDotLine(true);
        lineView.setShowPopup(LineView.SHOW_POPUPS_NONE);

        List<Double> watLevs =sh.getWaterLevels();
        List<Double> discharges =sh.getDisharges();

        ArrayList<Integer> dataList = new ArrayList<Integer>();
        for (int i = 0; i < sh.getWaterLevels().size(); i++) {
            dataList.add((int)Math.round(watLevs.get(i)));
        }
        ArrayList<Integer> dataList2 = new ArrayList<Integer>();
        for (int i = 0; i < randomint; i++) {
            dataList2.add((int)Math.round(discharges.get(i)));
        }

        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<ArrayList<Integer>>();
        dataLists.add(dataList);
        dataLists.add(dataList2);

        lineView.setDataList(dataLists);
    }

    public void initChart2(StationHistory sh){

        DataPoint[] discharges = new DataPoint[sh.getDisharges().size()];
        DataPoint[] watLevs = new DataPoint[sh.getWaterLevels().size()];
        ArrayList<String> dates = sh.getDates();
        for(int i=0;i<discharges.length;i++){

            discharges[i] =  new DataPoint(getDate(dates.get(i)).getTime(),sh.getDisharges().get(i));
            watLevs[i] =  new DataPoint(getDate(dates.get(i)).getTime(),sh.getWaterLevels().get(i));
        }

        LineGraphSeries<DataPoint> discharge = new LineGraphSeries<>(discharges);
        discharge.setColor(Color.RED);
        discharge.setTitle("Discharge");

        LineGraphSeries<DataPoint> waterLevel = new LineGraphSeries<>(watLevs);
        waterLevel.setColor(Color.BLUE);
        waterLevel.setTitle("Water Level");

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(),new SimpleDateFormat("K a")));
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        graph.getGridLabelRenderer().setHumanRounding(false);

        graph.addSeries(discharge);
        graph.addSeries(waterLevel);


    }
    private Date getDate(String date){

        StringBuilder sb = new StringBuilder(date);
        int length = (date.lastIndexOf("-"));
        date = sb.replace(length,date.length()-1,"").toString();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try{
            Date d = sdf.parse(date);
            return d;
        }catch (Exception e){
            Log.i("error", e.getLocalizedMessage().toString());
            return null;

        }
    }
private ArrayList<String>parseDates(ArrayList<String> dates){
    ArrayList<String> results = new ArrayList<>();
    try{
        for(int i=0;i<dates.size();i++){
            results.add(dates.get(i).substring(4,10));
        }
        return results;
    }catch(Exception e){
        return null;
    }
}

}
