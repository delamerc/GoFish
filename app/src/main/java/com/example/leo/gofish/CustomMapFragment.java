package com.example.leo.gofish;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Karlo on 11/15/2016.
 */

public class CustomMapFragment extends Fragment implements OnMapReadyCallback {
    private SupportMapFragment fragment;
    private GoogleMap mMap;
    List<Station> stations = new ArrayList<Station>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }
        fragment.getMapAsync(this);
        return inflater.inflate(R.layout.map_fragment, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (Station s : stations) {
            LatLng latLang = new LatLng(s.getLatitude(), s.getLongitude());
            if (s.isFavourite()) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLang)
                        .title(s.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            } else {
                mMap.addMarker(new MarkerOptions()
                        .position(latLang)
                        .title(s.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(43.6475, -79.8561));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(9);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }
    }

    public void init() {
        InputStream inputstream = getResources().openRawResource(R.raw.stations);
        CSVFile csv = new CSVFile(inputstream);
        stations = csv.read();

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        databaseConnector.open();
        for (Station s : stations) {
            Log.i("INFO", s.getId() + " station exists:" + databaseConnector.checkIfExists(s.getId()));
            if (databaseConnector.checkIfExists(s.getId())) {
                s.setFavourite(true);
            }
        }
        databaseConnector.close();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
