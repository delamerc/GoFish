package com.example.leo.gofish;

import java.io.Serializable;

/**
 * Created by Leo on 2016-11-19.
 */

public class Weather implements Serializable {
    private String summary;
    private String icon;
    private double latitude;
    private double longitude;

    private double temperature;
    private double apparentTemperature;
    private double windSpeed;
    private int windBearing;
    private double pressure;

    public Weather() {
        this.setLatitude(0);
        this.setLongitude(0);
        this.setSummary("");
        this.setIcon("");
        this.setTemperature(0);
        this.setApparentTemperature(0);
        this.setWindSpeed(0);
        this.setWindBearing(0);
        this.setPressure(0);
    }

    //detail weather
    public Weather(double latitude, double longitude, String summary, String icon, double temperature, double apparentTemperature, double windSpeed, int windBearing, double pressure) {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setSummary(summary);
        this.setIcon(icon);
        this.setTemperature(temperature);
        this.setApparentTemperature(apparentTemperature);
        this.setWindSpeed(windSpeed);
        this.setWindBearing(windBearing);
        this.setPressure(pressure);

    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public double getTemperature() {
        return temperature;
    }

    public double getTempCelcius() {
        return Math.round((5.0 / 9.0) * (getTemperature() - 32));
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }

    public double getFeelCelcius() {
        return Math.round((5.0 / 9.0) * (getApparentTemperature() - 32));
    }
    public void setApparentTemperature(double apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(int windBearing) {
        this.windBearing = windBearing;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public String temperatureToString() {
        return (Math.round(getTempCelcius()) + "C\u00B0");
    }

    public String apparentTempToString() {
        return (Math.round(getFeelCelcius()) + "C\u00B0");
    }

    public String pressureToString() {
        return String.format("%.2f", getPressure() / 10) + "kPA";
    }

    public String windToString() {
        return String.format("%.0f", getWindSpeed()) + "km/h " + DegreesToCardinal(getWindBearing());
    }
    private String DegreesToCardinal(double degrees)
    {
        String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[ (int)Math.round((  ((double)degrees % 360) / 45)) % 8 ];
    }

    private String DegreesToCardinalDetailed(double degrees)
    {
        degrees *= 10;
        String[] directions = { "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N" };
        return directions[(int)Math.round(((double)degrees % 3600) / 225)];
    }

}