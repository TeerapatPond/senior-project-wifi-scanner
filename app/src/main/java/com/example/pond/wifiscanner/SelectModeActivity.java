package com.example.pond.wifiscanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SelectModeActivity extends AppCompatActivity {
    //    private static String building;
    //    private static String floor;
    public final static String BUILDING_ID = "com.example.pond.wifiscanner.SelectModeActivity.buildingID";
    public final static String BUILDING_NAME = "com.example.pond.wifiscanner.SelectModeActivity.buildingName";
    public final static String FLOOR = "com.example.pond.wifiscanner.SelectModeActivity.floor";
    public final static String ROUND = "com.example.pond.wifiscanner.SelectModeActivity.round";
    public final static String FILE_NAME = "com.example.pond.wifiscanner.SelectModeActivity.fileName";
    public final static String MULTIPLE_SCAN = "com.example.pond.wifiscanner.SelectModeActivity.multipleScan";

    private EditText editTextBuildingID;
    private EditText editTextBuildingName;
    private EditText editTextFloor;
    private EditText editTextRound;
    private EditText editTextFileName;
    private EditText editTextMultipleScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editTextBuildingID = (EditText) findViewById(R.id.edittextBuidingID);
        editTextBuildingName = (EditText) findViewById(R.id.edittextBuildingName);
        editTextFloor = (EditText) findViewById(R.id.edittextFloor);
        editTextRound = (EditText) findViewById(R.id.edittextRound);
        editTextFileName = (EditText) findViewById(R.id.edittextFileName);
        editTextMultipleScan = (EditText) findViewById(R.id.edittextMultipleScan);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void collectFingerprintButton(View view) {
        EditText buildingET = (EditText) findViewById(R.id.message_building);
        String building = buildingET.getText().toString();
        EditText floorET = (EditText) findViewById(R.id.message_floor);
        String floor = floorET.getText().toString();
        Intent intent = new Intent(this, CollectFingerprintActivity.class);
        intent.putExtra(BUILDING_NAME, building);
        intent.putExtra(FLOOR, floor);
        startActivity(intent);
    }

    public void collectScanningButton(View view) {
        EditText buildingET = (EditText) findViewById(R.id.message_building);
        String building = buildingET.getText().toString();
        EditText floorET = (EditText) findViewById(R.id.message_floor);
        String floor = floorET.getText().toString();
        Intent intent = new Intent(this, CollectScanningActivity.class);
        intent.putExtra(BUILDING_NAME, building);
        intent.putExtra(FLOOR, floor);
        startActivity(intent);
    }

    public void buttonStart(View view) {
        if(canStart()) { // Information is ready.
            Intent i = new Intent(this, CollectFingerprintActivity.class);
            i.putExtra(BUILDING_ID, String.valueOf(editTextBuildingID.getText()));
            i.putExtra(BUILDING_NAME, String.valueOf(editTextBuildingName.getText()));
            i.putExtra(FLOOR, String.valueOf(editTextFloor.getText()));
            i.putExtra(ROUND, String.valueOf(editTextRound.getText()));
            i.putExtra(FILE_NAME, String.valueOf(editTextFileName.getText()) + ".txt");
            i.putExtra(MULTIPLE_SCAN, String.valueOf(editTextMultipleScan.getText()));
            startActivity(i);
        } else {
            showToast("Please fill every information before start");
        }
    }

    private boolean canStart() {
        if(     String.valueOf(editTextBuildingID.getText()).isEmpty() ||
                String.valueOf(editTextBuildingName.getText()).isEmpty() ||
                String.valueOf(editTextFloor.getText()).isEmpty() ||
                String.valueOf(editTextRound.getText()).isEmpty() ||
                String.valueOf(editTextFileName.getText()).isEmpty() ||
                String.valueOf(editTextMultipleScan.getText()).isEmpty()) {
            return false;
        }
        return true;
    }


    public void showToast(CharSequence message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

}
