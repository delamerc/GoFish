package com.example.leo.gofish;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Karlo on 11/15/2016.
 */

public class Station implements Serializable {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String province;
    private double waterLevel;
    private double discharge;
    private Weather weather;
    private StationHistory stationHistory;
    private String fileName;
    private boolean isFavourite;


    public Station() {
        this.id = "";
        this.name = "";
        this.latitude = 0;
        this.longitude = 0;
        this.province = "";
        this.waterLevel = 0;
        this.discharge = 0;
        this.weather = new Weather();
        this.isFavourite = false;
        this.stationHistory = new StationHistory();
    }

    public Station(String id, String name, double latitude, double longitude, String province) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
        this.waterLevel = 0;
        this.discharge = 0;
        this.weather = new Weather();
        this.stationHistory = new StationHistory();
        this.isFavourite = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public double getDischarge() {
        return discharge;
    }

    public void setDischarge(double discharge) {
        this.discharge = discharge;
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public StationHistory getStationHistory() {
        return stationHistory;
    }

    public void setStationHistory(StationHistory stationHistory) {
        this.stationHistory = stationHistory;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public String toString() {
        return name + ", " + province;
    }

    public int[] getDischargeHistory(){
        List<Double> dh = stationHistory.getDisharges();

        int x[] = new int[dh.size()];
        for(int i = 0; i < x.length; i++){
            x[i] = (int)Math.round(dh.get(i));
        }
        return x;
    }
    public int[] getWaterLevelHistory(){
        List<Double> wlh = stationHistory.getWaterLevels();

        int x[] = new int[wlh.size()];
        for(int i = 0; i < x.length; i++){
            x[i] = (int)Math.round(wlh.get(i));
        }
        return x;
    }
    public String[] getDatesHistory(){
        List<String> wlh = stationHistory.getDates();

        String x[] = new String[wlh.size()];
        for(int i = 0; i < x.length; i++){
            x[i] = wlh.get(i).toString();
        }
        return x;
    }

}
