package com.example.pond.wifiscanner;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.SystemClock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Scanning extends AsyncTask<Void, JSONObject, Void[]> {

    private ScanningListener scanningListener;
    private WifiManager wifi;
    private ArrayList<AccessPoint> orderedAP;
    private boolean is_wifi_ready = false;
    private int multipleScan;

    private long last_buffer_time = 0;

    private int pause_before_scanning = 1000; // Milli-seconds
    private int pause_check_buffer = 100; // Milli-seconds
    private int pause_after_scanning = 500; // Milli-seconds

    @SuppressLint("MissingPermission")
    public Scanning(ScanningListener scanningListener, WifiManager wifi, int multipleScan) {
        this.scanningListener = scanningListener;
        this.wifi = wifi;
        this.multipleScan = multipleScan;
    }

    @Override
    protected Void[] doInBackground(Void... voids) {
        pause(pause_before_scanning);
        setBufferTime();
        for(int i = 0;i < multipleScan;i++) {
            setAccessPointList();
            while (true) {
                if (is_ready()) {
//                    publishProgress(bufferLifespan);
                    JSONObject result = new JSONObject();
                    result.put("access_point", arrayListToJSONArray());
                    result.put("buffer_time", last_buffer_time);
                    result.put("times", i + 1);
                    is_wifi_ready = false;
                    publishProgress(result);
                    pause(pause_after_scanning);
                    break;
                } else { // Obsoleted scanning results in the buffer
                    pause(pause_check_buffer);
                }
            }
        }
        return voids;
    }

    private boolean is_ready() {
        if(!is_wifi_ready) {
            setAccessPointList();
        }
        return is_wifi_ready;
    }

    @Override
    protected void onProgressUpdate(JSONObject... scanResult) {
        scanningListener.onUpdateScanningTimes(scanResult[0]);
    }

    @Override
    protected void onPostExecute(Void... voids) {
        scanningListener.onCompleteScanning();
    }

    private void setAccessPointList() {
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            if (wifi.startScan()) {
                List<ScanResult> results = wifi.getScanResults();
                scanResultToArrayList(results);
            }
        }
    }

    private JSONArray arrayListToJSONArray() {
        JSONArray jsonAP = new JSONArray();
        for (int i = 0; i < orderedAP.size(); i++) {
            JSONObject accessPoint = new JSONObject();
            accessPoint.put("BSSID", orderedAP.get(i).getBSSID());
            accessPoint.put("SSID", orderedAP.get(i).getSSID());
            accessPoint.put("RSSI", orderedAP.get(i).getRSSI());
            accessPoint.put("frequency", orderedAP.get(i).getFrequency());
            jsonAP.add(accessPoint);
        }
        return jsonAP;
    }

    private void setBufferTime() {
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            if (wifi.startScan()) {
                List<ScanResult> results = wifi.getScanResults();
                if (results.size() > 0) {
                    last_buffer_time = results.get(0).timestamp / 1000;
                }
            }
        }
    }

    private void scanResultToArrayList(List<ScanResult> results) {
        ArrayList<AccessPoint> listAP = new ArrayList<AccessPoint>();
        long lastScan = 0;
        for (int i = 0; i < results.size(); i++) {
            listAP.add(new AccessPoint(results.get(i).BSSID, results.get(i).SSID, results.get(i).level, results.get(i).frequency));
            lastScan = results.get(i).timestamp / 1000;
        }

        if (lastScan > last_buffer_time) {
            is_wifi_ready = true;
            last_buffer_time = lastScan;
        }

        Collections.sort(listAP, new RSSIComparator());
        orderedAP = listAP;
    }

    private void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            return;
        }
    }
}
