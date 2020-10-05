package com.example.pond.wifiscanner;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.indoorlocalization.Localization;
import com.example.indoorlocalization.OnTaskCompleteListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class LocalizationActivity extends AppCompatActivity {

    TextView tvLastResult;
    TextView tvLocalizationResult;
    Localization localization;
    long lastScan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localization);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startScan(View view) {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        int state = wifi.getWifiState();
        JSONObject fp = new JSONObject();
        if (state == WifiManager.WIFI_STATE_ENABLED) {
            if (wifi.startScan()) {
                List<ScanResult> results = wifi.getScanResults();
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < results.size(); i++) {
                    JSONObject accesspoint = new JSONObject();
                    accesspoint.put("BSSID", results.get(i).BSSID);
                    accesspoint.put("SSID", results.get(i).SSID);
                    accesspoint.put("RSSI", results.get(i).level + "");
                    jsonArray.add(accesspoint);
                    lastScan = results.get(i).timestamp / 1000000;
                }
                fp.put("user_id", "Teerapat-Testing");
                fp.put("latitude", "13.74602646");
                fp.put("longitude", "100.5340422");
                fp.put("ap", jsonArray);
                tvLastResult.setText((SystemClock.elapsedRealtime() / 1000) - lastScan + " seconds ago");
            }
        }

        OnTaskCompleteListener completeListener = new OnTaskCompleteListener() {
            @Override
            public void onCompleteListerner(JSONObject result) {
                // Update your UI here
                String show = (SystemClock.elapsedRealtime() / 1000) + "\n";
                show += "Faculty id: " + result.get("faculty_id") + "\t\n";
                show += "Building id: " + result.get("building_id") + "\t\n";
                show += "Building name: " + result.get("building_name") + "\t\n";
                show += "Floor id: " + result.get("floor") + "\t\n";
                show += "Room number: " + result.get("room_number");
                tvLocalizationResult.setText(show);
            }
        };

        Localization localization = new Localization(completeListener);
        localization.execute(fp);
    }

    public void initialize() {
        OnTaskCompleteListener completeListener = new OnTaskCompleteListener() {
            @Override
            public void onCompleteListerner(JSONObject value) {
                tvLocalizationResult.setText((SystemClock.elapsedRealtime() / 1000) - lastScan + "");
            }
        };

        tvLastResult = (TextView) findViewById(R.id.tvLastResult);
        tvLocalizationResult = (TextView) findViewById(R.id.tvLocalizationResult);
        tvLocalizationResult.setMovementMethod(new ScrollingMovementMethod());
        localization = new Localization(completeListener);
    }
}
