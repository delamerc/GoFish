package com.example.leo.gofish;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 2016-11-30.
 */

public class StationHistory implements Serializable {

    private List<Double> disharges;
    private List<Double> waterLevels;
    private ArrayList<String> dates;

    public StationHistory(){
        this.setDisharges(new ArrayList<Double>());
        this.setWaterLevels(new ArrayList<Double>());
        this.setDates(new ArrayList<String>());
    }


    public List<Double> getDisharges() {
        return disharges;
    }

    public void setDisharges(List<Double> disharges) {
        this.disharges = disharges;
    }

    public List<Double> getWaterLevels() {
        return waterLevels;
    }

    public void setWaterLevels(List<Double> waterLevels) {
        this.waterLevels = waterLevels;
    }
    public ArrayList<String> getDates() {
        return dates;
    }
    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
    }
}
