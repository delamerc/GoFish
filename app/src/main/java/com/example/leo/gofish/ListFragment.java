package com.example.leo.gofish;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karlo on 11/15/2016.
 */

public class ListFragment extends Fragment {
    List<Station> stations = new ArrayList<Station>();
    private final String TAG = this.getClass().getName();
    ArrayList<Station> stations = new ArrayList<Station>();
    ArrayList<Station> favStations = new ArrayList<Station>();
    CustomAdapter adapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        displayDefaultStation();
    }

    private class CustomAdapter extends ArrayAdapter<Station> {
        private ArrayList<Station> stationList;
        private Context mContext;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<Station> stations) {
            super(context, textViewResourceId, stations);
            this.mContext = context;
            this.stationList = new ArrayList<Station>();
            this.stationList.addAll(stations);
        }

        public void setList(ArrayList<Station> favs) {
            this.stationList = favs;
        }

        private class ViewHolder {
            TextView name;
            CheckBox isFavourite;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.custom_list_textview, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.station);
                holder.isFavourite = (CheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
                holder.isFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view;
                        Station station = (Station) cb.getTag();
                        if(cb.isChecked()) {
                            station.setFavourite(true);
                            addToFavourites(station);
                            favStations.add(station);
                        }
                        else {
                            station.setFavourite(false);
                            removeFromFavourites(station);
                            favStations.remove(station);
                        }
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            Station station = stationList.get(position);
            holder.name.setText(station.toString());
            holder.isFavourite.setChecked(station.isFavourite());
            holder.isFavourite.setTag(station);
            return convertView;
        }
    }

    public void init() {
        InputStream inputstream = getResources().openRawResource(R.raw.stations);
        CSVFile csv = new CSVFile(inputstream);
        stations = csv.read();

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        databaseConnector.open();
        for(Station s : stations) {
            Log.i("INFO", s.getId() + " station exists:" + databaseConnector.checkIfExists(s.getId()));
            if(databaseConnector.checkIfExists(s.getId())) {
                favStations.add(s);
                s.setFavourite(true);
            }
        }
        databaseConnector.close();
    }

    private void displayDefaultStation() {
        adapter = new CustomAdapter(getActivity(), R.layout.list_fragment, stations);
        ListView listView = (ListView) getActivity().findViewById(R.id.station_list);
        listView.setAdapter(adapter);

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

    private void addToFavourites(Station s) {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        databaseConnector.insertStation(s.getId(), s.getName(), s.getProvince());
    }

    private void removeFromFavourites(Station s) {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        databaseConnector.deleteStation(s.getId());
    }
}
