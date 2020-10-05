package com.example.pond.wifiscanner;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SignalMeasurementActivity extends AppCompatActivity {

    private int delay = 7000;
    WifiManager wifi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_measurement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(SignalMeasurementActivity.this,
                new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE}, 1);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startScan(View view) {
        ImageView circle = (ImageView) findViewById(R.id.circle);
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(delay);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
                scanWiFi();
            }

            public void onAnimationEnd(Animation animation) {
            }
        });
        circle.startAnimation(rotateAnimation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            setDeviceName();
//            textViewDevice.setText(deviceName);
        }
    }

    public void scanWiFi() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int state = wifi.getWifiState();
        EditText et = (EditText) findViewById(R.id.ssid);
        String ssid = String.valueOf(et.getText());

        if (state == WifiManager.WIFI_STATE_ENABLED) {
            if (wifi.startScan()) {
                List<ScanResult> results = wifi.getScanResults();
                int size = results.size(); //Number of access point in this fingerprint
                System.out.println(size);
                String output = "";
                for (int i = 0; i < size; i++) {
                    if (ssid.equals("*")) {
                        output += results.get(i).SSID + "\n";
                        output += results.get(i).BSSID + "\n";
                        output += results.get(i).level + "\n";
                        output += "\n";
                    } else {
                        if (results.get(i).SSID.equalsIgnoreCase(ssid)) {
                            output += results.get(i).SSID + "\n";
                            output += results.get(i).BSSID + "\n";
                            output += results.get(i).level + "\n";
                            output += "\n";
                        }
                    }
                }
                TextView result = (TextView) findViewById(R.id.result);
                System.out.println("pond" + output);
                result.setText(output);
            }
        }
    }

    private long setBufferTime() {
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            if (wifi.startScan()) {
                List<ScanResult> results = wifi.getScanResults();
                if (results.size() > 0) {
                    long last_buffer_time = results.get(0).timestamp / 1000;
                    return last_buffer_time;
                }
            }
        }
        return 0;
    }

    public void samplingRate(View view) {
        int samplingTimes = 100;
        long[] timestamp = new long[samplingTimes];
        long last_buffer_time = setBufferTime();

        int index = 0;

        while(true) {
            if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                if (wifi.startScan()) {
                    List<ScanResult> results = wifi.getScanResults();
                    if (results.size() > 0) {
                        long buffer_time = results.get(0).timestamp / 1000;
                        if (buffer_time > last_buffer_time) {
                            timestamp[index] = buffer_time;
                            index++;
                            last_buffer_time = buffer_time;
                            Log.i("Teerapat", index + "");
                        }
                    }
                }
            }
            if (index >= samplingTimes) break;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }

        String timestamp_report = "";
        for (int i = 0;i < samplingTimes;i++) {
            timestamp_report += timestamp[i] + ",";
        }
        Log.i("Teerapat", timestamp_report);
    }
}
