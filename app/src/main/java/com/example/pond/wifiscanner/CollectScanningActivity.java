package com.example.pond.wifiscanner;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectScanningActivity extends AppCompatActivity {
    private static String building;
    private static String floor;
    private static ImageView circle;
    private static EditText tagNumber;
    private static TextView area;
    private static int delay = 7000; //Refresh time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        building = intent.getStringExtra(SelectModeActivity.BUILDING_NAME);
        floor = intent.getStringExtra(SelectModeActivity.FLOOR);
        String title = "Scanning " + floor + "th " + building;
        setContentView(R.layout.activity_collect_scanning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void decreaseTagButton(View view) {
        tagNumber = (EditText) findViewById(R.id.tag_number);
        String tag = String.valueOf(tagNumber.getText());
        if(tag.equals("")) {
            tagNumber.setText("0");
        } else {
            String newTagNumber = (Integer.parseInt(tag) - 1) + "";
            tagNumber.setText(newTagNumber);
        }
        tag = String.valueOf(tagNumber.getText());
    }

    public void increaseTagButton(View view) {
        tagNumber = (EditText) findViewById(R.id.tag_number);
        String tag = String.valueOf(tagNumber.getText());
        if(tag.equals("")) {
            tagNumber.setText("0");
        } else {
            String newTagNumber = (Integer.parseInt(tag) + 1) + "";
            tagNumber.setText(newTagNumber);
        }
        tag = String.valueOf(tagNumber.getText());
    }

    public void indoorButton(View view) {
        area = (TextView) findViewById(R.id.area);
        area.setText("Indoor");
    }

    public void outdoorButton(View view) {
        area = (TextView) findViewById(R.id.area);
        area.setText("Outdoor");
    }

    public String getArea() {
        area = (TextView) findViewById(R.id.area);
        return String.valueOf(area.getText());
    }

    public String getTag() {
        tagNumber = (EditText) findViewById(R.id.tag_number);
        String tag = String.valueOf(tagNumber.getText());
        if(tag.equals("")) {
            return "-1";
        } else {
            return tag;
        }
    }

//    public void collectScanningButton(View view) {
//        circle = (ImageView) findViewById(R.id.arrow);
//        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setDuration(delay);
//        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
//            public void onAnimationStart(Animation animation) {
//            }
//
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            public void onAnimationEnd(Animation animation) {
//                scanWiFi();
//                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                v.vibrate(1000);
//            }
//        });
//        circle.startAnimation(rotateAnimation);
//    }

//    public void scanWiFi() {
//        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        int state = wifi.getWifiState();
//        if(state == WifiManager.WIFI_STATE_ENABLED) {
//            if(wifi.startScan()) {
//                List<ScanResult> results = wifi.getScanResults();
//                Collections.sort(results, new RSSIComparator());
//                int size = results.size(); //Number of access point in this fingerprint
//
//                String output = floor + "\n"; //In every fingerprint, Start with floor number, number of access point and followed with BSSID, SSID and RSSI for each access point
//                output += getTag() + "\n";
//                output += getArea() + "\n";
//                output += size + "\n";
//                for(int i = 0;i < size;i++) {
//                    output += results.get(i).BSSID + " ";
//                    output += results.get(i).SSID.replaceAll(" ", "") + " ";
//                    output += results.get(i).level + "\n";
//                }
//
//                String fileName = building + "Scanning" + ".txt";
//                try {
//                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/sdcard/" + fileName, true)));
//                    out.print(output);
//                    out.flush();
//                    out.close();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                TextView collectScanning = (TextView)findViewById(R.id.collectScanning);
//                collectScanning.setText(output);
//            }
//        }
//    }

}

