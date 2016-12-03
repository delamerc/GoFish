package com.example.leo.gofish;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Karlo on 12/2/2016.
 */

public class FavouriteListFragment extends Fragment {
    private final String TAG = this.getClass().getName();
    ArrayList<Station> stations = new ArrayList<Station>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fav_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        displayDefaultStation();
    }

    public void init() {
        InputStream inputstream = getResources().openRawResource(R.raw.stations);
        CSVFile csv = new CSVFile(inputstream);
        ArrayList<Station> favStations = csv.read();

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        databaseConnector.open();
        for(Station s : favStations) {
            Log.i("INFO", s.getId() + " station exists:" + databaseConnector.checkIfExists(s.getId()));
            if(databaseConnector.checkIfExists(s.getId())) {
                s.setFavourite(true);
                stations.add(s);
            }
        }
        databaseConnector.close();
    }

    private void displayDefaultStation() {
        ListView listView = (ListView) getActivity().findViewById(R.id.fav_station_list);
        ArrayAdapter favAdapter = new ArrayAdapter(getActivity(), R.layout.custom_favlist, stations);
        listView.setAdapter(favAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Station s = (Station) adapterView.getItemAtPosition(i);
                Fragment frag = new DetailFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Station", s);
                frag.setArguments(bundle);

                Fragment fr = frag;
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, fr);
                ft.commit();
            }
        });
    }

}
