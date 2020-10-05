package com.example.pond.wifiscanner;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public final static String BUILDING_NAME = "com.example.pond.wifiscanner.MainActivity.buildingname";
    public final static String BUILDING_FLOOR = "com.example.pond.wifiscanner.MainActivity.buildingfloor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startButton(View view) {
        Intent intent = new Intent(this, SelectModeActivity.class);
//        EditText buildingET = (EditText) findViewById(R.id.message_building);
//        String building = buildingET.getText().toString();
//        EditText floorET = (EditText) findViewById(R.id.message_floor);
//        String floor = floorET.getText().toString();
//        intent.putExtra(BUILDING_NAME, building);
//        intent.putExtra(BUILDING_FLOOR, floor);
        startActivity(intent);
    }

    public void wifiInfo(View view) {
        Intent intent = new Intent(this, WiFiInfoActivity.class);
        startActivity(intent);
    }

    public void signalMeasurement(View view) {
        Intent intent = new Intent(this, SignalMeasurementActivity.class);
        startActivity(intent);
    }

    public void localization(View view) {
        Intent intent = new Intent(this, LocalizationActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.debug) {
            debug();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void debug() {
        ArrayList<TimeStampScanResults> ts = new ArrayList<TimeStampScanResults>();
        for (int lap = 1; lap <= 2000; lap++) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int state = wifi.getWifiState();
            if (state == WifiManager.WIFI_STATE_ENABLED) {
                if (wifi.startScan()) {
                    ts.add(new TimeStampScanResults(SystemClock.uptimeMillis(), wifi.getScanResults()));
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*String fileName = "FindInterval.txt";
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/sdcard/" + fileName, true)));
            for(int i = 0;i < ts.size();i++) {
                out.print(ts.get(i).getTimeStamp() + "\n"); //Print timestamp, follow with number of access point and follow with detail of each access point.
                out.print(ts.get(i).size() + "\n");
                for(int j = 0; j < ts.get(i).size();j++) {
                    out.print(ts.get(i).getScanResults().get(j).BSSID + " " + ts.get(i).getScanResults().get(j).level + "\n");
                }
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        int lastScan = 0;
        long lastTime = ts.get(0).getTimeStamp();
        int lastLevel = ts.get(0).getScanResults().get(0).level;
        for (int i = 1; i < ts.size(); i++) {
            long tempTime = ts.get(i).getTimeStamp();
            int tempLevel = ts.get(i).getScanResults().get(0).level;
            if (lastLevel != tempLevel) {
                System.out.println("Diff Scan " + (i - lastScan));
                System.out.println("Diff Time" + (tempTime - lastTime));
                lastScan = i;
                lastTime = tempTime;
                lastLevel = tempLevel;
            }
        }

        System.out.println("Done");
    }
}