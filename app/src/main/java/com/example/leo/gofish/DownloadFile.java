package com.example.leo.gofish;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Karlo on 11/15/2016.
 */

class DownloadFile extends AsyncTask<Station, Void, Station> {
    private Context mContext;
    private static final String TAG = "Downloader";
    private ProgressDialog mProgressDialog;
    public AsyncDLResponse delegate = null;

    public DownloadFile(Context ctx) {
        mContext = ctx;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        Log.i(TAG, "Initiate download...");
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
    }

    @Override
    protected Station doInBackground(Station... objects) {
        Station s = (Station) objects[0];
        try {
            File dir = new File(mContext.getFilesDir() + "/stations/hourly");

            if(dir.exists() == false) { // checks if directory is available otherwise create one
                dir.mkdirs();
            }

            String timeStamp = getTimestamp();
            Log.i(TAG, "Timestamp: " + timeStamp);

            String url = s.getProvince() + "_" +
                    s.getId() + "_hourly_hydrometric.csv"; // url file name

            String fileName =  timeStamp + "_" +
                    s.getProvince() + "_" +
                    s.getId() + "_hourly_hydrometric.csv"; // filename to be saved
            s.setFileName(fileName);
            s.setWeather(getWeather(s.getLatitude(),s.getLongitude()));

            File file = new File(dir, fileName);
            if(!file.exists() && !file.isFile()) {
               // mProgressDialog = ProgressDialog.show(mContext, "Download", "Downloading station data...", true); // show download progress bar

                URL u = new URL("http://dd.weather.gc.ca/hydrometric/csv/ON/hourly/" + url); // url of website with csv files
                URLConnection conn = u.openConnection();
                int contentLength = conn.getContentLength();

                DataInputStream stream = new DataInputStream(u.openStream());

                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();

                DataOutputStream dos = new DataOutputStream(new FileOutputStream(file)); // save file
                dos.write(buffer);
                dos.flush();
                dos.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.i(TAG, "File not found: " + e.toString());
        }
        catch (IOException e) {
            Log.i(TAG, "File not found: " + e.toString());
        }
        return s;
    }

    @Override
    protected void onPostExecute(Station s) {
        super.onPostExecute(s);
        delegate.onTaskComplete(s);
        mProgressDialog.dismiss();
        Log.i(TAG, "Download successful!");
    }

    public String getTimestamp() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        return "" + year + month + day + hour;
    }

    private final String key = "e281e3e3f062b04bae195d56fb181e1a/";
    private final String url = "https://api.forecast.io/forecast/";

    private Weather getWeather(double lat, double lng){
        Weather weather=null;
        String reqUrl = url + key+lat+","+lng;

        HTTPHandler http = new HTTPHandler();

        String jsonStr = http.makeServiceCall(reqUrl);

        if (jsonStr != null) {
            try {
                JSONObject weatherObj = new JSONObject(jsonStr);
                JSONObject conditions = weatherObj.getJSONObject("currently");

                weather = new Weather(
                        weatherObj.getDouble("latitude"),
                        weatherObj.getDouble("longitude"),
                        conditions.getString("summary"),
                        conditions.getString("icon"),
                        conditions.getDouble("temperature"),
                        conditions.getDouble("apparentTemperature"),
                        conditions.getDouble("windSpeed"),
                        conditions.getInt("windBearing"),
                        conditions.getDouble("pressure")
                );

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json.");
        }

        return weather;

    }

}
