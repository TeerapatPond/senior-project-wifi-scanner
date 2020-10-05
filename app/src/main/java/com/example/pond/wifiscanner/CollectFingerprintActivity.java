package com.example.pond.wifiscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CollectFingerprintActivity extends AppCompatActivity {
    private static String building;
    private static ImageView circle;
    private static EditText tagNumber;
    private static TextView area;
    private static int delay = 7000; //Refresh time

    private static boolean isShowToast = true;
    private static String filePath = "/sdcard/WiFiSurvey/";
    Vibrator vibrator;
    int vibrateLength = 200;
    String deviceName = "";

    private static WifiManager wifi;

    private static String buildingID;
    private static String buildingName;
    private static String floor;
    private static String round;
    private static String fileName;
    private static int multipleScan;

    private TextView textViewBuildingID;
    private TextView textViewBuildingName;
    private TextView textViewFloor;
    private TextView textViewRound;
    private TextView textViewDevice;
    private TextView textViewFileName;

    private EditText editTextTag;
    private TextView textViewEnvironment;

    private TextView textViewStatus;
    private ImageView imageViewStatus;

    private ImageView buttonMinus;
    private ImageView buttonPlus;

    private Button buttonScan;

    private TextView textViewScanningStatus;
    private TextView textViewScanningResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_collect_fingerprint);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialActivity(intent);
    }

    private void initialActivity(Intent intent) {
        getIntent(intent);
        assignView();
        textViewBuildingID.setText(buildingID);
        textViewBuildingName.setText(buildingName);
        textViewFloor.setText(floor);
        textViewRound.setText(round);
//        textViewDevice.setText(getDeviceName()); TODO
        textViewFileName.setText(fileName);

        textViewBuildingID.setSelected(true);
        textViewBuildingName.setSelected(true);
        textViewFloor.setSelected(true);
        textViewRound.setSelected(true);
        textViewDevice.setSelected(true);
        textViewFileName.setSelected(true);
        textViewEnvironment.setSelected(true);
        textViewStatus.setSelected(true);
        setTitle(buildingName);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ActivityCompat.requestPermissions(CollectFingerprintActivity.this,
                new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    private void assignView() {
        textViewBuildingID = (TextView) findViewById(R.id.textViewBuildingID);
        textViewBuildingName = (TextView) findViewById(R.id.textViewBuildingName);
        textViewFloor = (TextView) findViewById(R.id.textViewFloor);
        textViewRound = (TextView) findViewById(R.id.textViewRound);
        textViewDevice = (TextView) findViewById(R.id.textViewDeviceName);
        textViewFileName = (TextView) findViewById(R.id.textViewFileName);
        editTextTag = (EditText) findViewById(R.id.editTextTag);
        textViewEnvironment = (TextView) findViewById(R.id.textViewEnvironment);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        imageViewStatus = (ImageView) findViewById(R.id.imageViewStatus);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewScanningResult = (TextView) findViewById(R.id.textViewScanningResult);
        textViewScanningStatus = (TextView) findViewById(R.id.textViewScanningStatus);
        buttonMinus = (ImageView) findViewById(R.id.buttonMinus);
        buttonPlus = (ImageView) findViewById(R.id.buttonPlus);
    }


    private void getIntent(Intent intent) {
        buildingID = intent.getStringExtra(SelectModeActivity.BUILDING_ID);
        buildingName = intent.getStringExtra(SelectModeActivity.BUILDING_NAME);
        floor = intent.getStringExtra(SelectModeActivity.FLOOR);
        round = intent.getStringExtra(SelectModeActivity.ROUND);
        fileName = intent.getStringExtra(SelectModeActivity.FILE_NAME);
        multipleScan = Integer.parseInt(intent.getStringExtra(SelectModeActivity.MULTIPLE_SCAN));
    }

    public void minusButton(View view) {
        String tag = String.valueOf(editTextTag.getText());
        try { // Tag is integer
            int tagNumber = Integer.parseInt(tag);
            tagNumber--;
            editTextTag.setText(tagNumber + "");

        } catch (NumberFormatException e) { // Tag is not integer
            showToast("Tag is not a number.");
        }
    }

    public void plusButton(View view) {
        String tag = String.valueOf(editTextTag.getText());
        try { // Tag is integer
            int tagNumber = Integer.parseInt(tag);
            tagNumber++;
            editTextTag.setText(tagNumber + "");

        } catch (NumberFormatException e) { // Tag is not integer
            showToast("Tag is not a number.");
        }
    }

    public void indoorButton(View view) {
        textViewEnvironment.setText("Indoor");
    }

    public void outdoorButton(View view) {
        textViewEnvironment.setText("Outdoor");
    }

    public void buttonScan(View view) {
        if (canScan()) {
            textViewStatus.setText("Scanning, please wait ... (0/" + multipleScan + ")");
            buttonScan.setBackgroundResource(R.drawable.button_background_round_corner_grey);
            buttonScan.setText("SCANNING...");
            imageViewStatus.setImageResource(R.drawable.alert);
            textViewScanningStatus.setText("Scanning status");
            ScanningListener listener = new ScanningListener() {

                @Override
                public void onCompleteScanning() {
                    textViewStatus.setText("Scan completed");
                    buttonScan.setBackgroundResource(R.drawable.button_background_round_corner_red);
                    buttonScan.setText("SCAN");
                    imageViewStatus.setImageResource(R.drawable.check);

                    plusButton(buttonPlus);
//                    JSONObject fingerprint = createFingerprint(scanResult);
//                    textViewScanningResult.setText(formatJSONtoString(fingerprint.toString()).trim());
//                    saveToFile(fingerprint.toString());
                }

                @Override
                public void onUpdateBufferLifespan(long second) {
                    if (second == 1) {
                        textViewStatus.setText("Buffer lifespan: " + second + " milli-second ago");
                    } else {
                        textViewStatus.setText("Buffer lifespan: " + second + " milli-seconds ago");
                    }
                }

                @Override
                public void onUpdateScanningTimes(JSONObject scanResult) {

                    JSONObject fingerprint = createFingerprint(scanResult);
                    long bufferTime = (long) scanResult.get("buffer_time");
                    int times = (int) scanResult.get("times");
                    String oldText = (String) textViewScanningStatus.getText();
                    textViewScanningStatus.setText(oldText + "\n" + "[" + times + "]" + "\t" +
                            "Buffer at " + bufferTime + "\n\t\t\t" +
                            "Timestamp: " + fingerprint.get("timestamp") + "\n\t\t\t" +
                            "Size: " + ((JSONArray) fingerprint.get("access_point")).size());
                    textViewScanningResult.setText(formatJSONtoString(fingerprint.toString()).trim());

                    textViewStatus.setText("Scanning, please wait ... (" + times + "/" + multipleScan + ")");

                    saveToFile(fingerprint.toString());
                }
            };

            Scanning scanning = new Scanning(listener, wifi, multipleScan);
            scanning.execute();
        }
    }

    private void saveToFile(String output) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath + fileName, true)));
            out.print(output + "\n");
            out.flush();
            out.close();
            vibrator.vibrate(vibrateLength);
        } catch (Exception e_folder) {
            File dir = new File(filePath);
            if (dir.mkdirs()) {
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath + fileName, true)));
                    out.print(output + "\n");
                    out.flush();
                    out.close();
                    vibrator.vibrate(vibrateLength);
                } catch (Exception e_file) {
                    showToast("Cannot write to file." + e_file.toString());
                }
            } else {
                showToast("Cannot create folder. " + e_folder.toString());
            }
        }
    }

    private String formatJSONtoString(String text) {
        StringBuilder json = new StringBuilder();
        String indentString = "";
        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }
        return json.toString();
    }

    private JSONObject createFingerprint(JSONObject scanResult) {
        JSONObject fingerprint = new JSONObject();
        fingerprint.put("building_id", buildingID);
        fingerprint.put("building_name", buildingName);
        fingerprint.put("floor", floor);
        fingerprint.put("round", round);
        fingerprint.put("device_name", deviceName);
        fingerprint.put("tag", String.valueOf(editTextTag.getText()));
        fingerprint.put("environment", String.valueOf(textViewEnvironment.getText()).toLowerCase());
        fingerprint.put("timestamp", getTime());
        fingerprint.put("access_point", scanResult.get("access_point"));
        return fingerprint;
    }


    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private boolean canScan() {
        if (String.valueOf(editTextTag.getText()).isEmpty() ||
                String.valueOf(textViewStatus.getText()).equals("Please select the environment")) {
            showToast("Please fill every information before scanning");
            return false;
        } else {
            if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
                showToast("Please turn Wi-Fi on");
                return false;
            } else {
                if (String.valueOf(buttonScan.getText()).equalsIgnoreCase("SCANNING...")) {
                    showToast("Currently scanning");
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void setDeviceName() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceName = Build.MODEL + " IMEI:" + telephonyManager.getDeviceId();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setDeviceName();
            textViewDevice.setText(deviceName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void decreaseTagButton(View view) {
        tagNumber = (EditText) findViewById(R.id.tag_number);
        String tag = String.valueOf(tagNumber.getText());
        if (tag.equals("")) {
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
        if (tag.equals("")) {
            tagNumber.setText("0");
        } else {
            String newTagNumber = (Integer.parseInt(tag) + 1) + "";
            tagNumber.setText(newTagNumber);
        }
        tag = String.valueOf(tagNumber.getText());
    }

//    public void indoorButton(View view) {
//        area = (TextView) findViewById(R.id.area);
//        area.setText("Indoor");
//    }
//
//    public void outdoorButton(View view) {
//        area = (TextView) findViewById(R.id.area);
//        area.setText("Outdoor");
//    }

    public String getArea() {
        area = (TextView) findViewById(R.id.area);
        return String.valueOf(area.getText());
    }

    public String getTag() {
        tagNumber = (EditText) findViewById(R.id.tag_number);
        String tag = String.valueOf(tagNumber.getText());
        if (tag.equals("")) {
            return "-1";
        } else {
            return tag;
        }
    }

//    public void collectFingerprintButton(View view) {
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
//                String log = scanWiFi("NONE", building, floor, 0, getTag(), getArea());
//                saveLog(log);
//                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                v.vibrate(1000);
//            }
//        });
//        circle.startAnimation(rotateAnimation);
//    }

//    public void showInfo(JSONObject msg) {
//        TextView collectFingerprint;
//        String show = "";
//        show += "Faculty:\t" + msg.get("faculty") + "\n";
//        show += "Builind:\t" + msg.get("building_id") + "\n";
//        show += "Floor number:\t" + msg.get("floor_number") + "\n";
//        show += "Round:\t" + msg.get("round") + "\n";
//        show += "Place:\t" + msg.get("place") + "\n";
//        show += "Environment:\t" + msg.get("environment") + "\n";
//        show += "Time:\t" + msg.get("timestamp") + "\n";
//        show += "Access Point:\t" + msg.get("ap");
//        collectFingerprint = (TextView) findViewById(R.id.collectFingerprint);
//        collectFingerprint.setText(show);
//    }

    public void saveLog(String msg) {
        String fileName = "JSONlog" + ".txt";
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/sdcard/" + fileName, true)));
            out.print(msg);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public String scanWiFi(String faculty, String building_id, String floor, int round, String place, String environment) {
//        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        int state = wifi.getWifiState();
//        String timeStamp = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
//        JSONObject toLog = new JSONObject();
//        toLog.put("faculty", faculty);
//        toLog.put("building_id", building_id);
//        toLog.put("floor_number", floor);
//        toLog.put("round", round);
//        toLog.put("place", place);
//        toLog.put("environment", environment);
//        toLog.put("timestamp", timeStamp);
//        JSONArray apList = new JSONArray();
//        if (state == WifiManager.WIFI_STATE_ENABLED) {
//            if (wifi.startScan()) {
//                List<ScanResult> results = wifi.getScanResults();
//                Collections.sort(results, new RSSIComparator());
//                int size = results.size();
//                for (int i = 0; i < size; i++) {
//                    JSONObject ap = new JSONObject();
//                    ap.put("BSSID", results.get(i).BSSID);
//                    ap.put("SSID", results.get(i).SSID);
//                    ap.put("RSSI", results.get(i).level);
//                    ap.put("frequency", results.get(i).frequency);
//                    apList.add(ap);
//                }
//                toLog.put("ap", apList);
//            }
//        }
//        showInfo(toLog);
//        return toLog.toString() + "\n";
//    }

    public void showToast(CharSequence message) {
        if (isShowToast) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
        }
    }
}
