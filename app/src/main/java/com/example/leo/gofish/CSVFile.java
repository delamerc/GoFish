package com.example.leo.gofish;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karlo on 11/15/2016.
 */

public class CSVFile {
    InputStream inputStream;

    public CSVFile(InputStream inputstream) {
        this.inputStream = inputstream;
    }

    public ArrayList<Station> read() {
        ArrayList<Station> result = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                Station station = new Station(row[0], row[1], Double.parseDouble(row[2]), Double.parseDouble(row[3]), row[4]);
                result.add(station);
            }

        } catch (IOException e){
            throw new RuntimeException("Error in reading CSV file: "+ e);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return result;
    }

    public Station readStation(Station s) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String last = "", line;
            while((line = reader.readLine()) != null) {
                last = line;
            }
            String [] data = last.split(",", -1);
            if(data[2].isEmpty()) {
                s.setWaterLevel(0);
            } else {
                s.setWaterLevel(Double.parseDouble(data[2]));
            }
            if(data[6].isEmpty()) {
                s.setDischarge(0);
            } else {
                s.setDischarge(Double.parseDouble(data[6]));
            }
        } catch (IOException e){
            throw new RuntimeException("Error in reading CSV file: "+ e);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return s;
    }
}
