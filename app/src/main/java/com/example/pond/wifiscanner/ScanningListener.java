package com.example.pond.wifiscanner;

import org.json.simple.JSONObject;

public interface ScanningListener {
    void onCompleteScanning();
    void onUpdateBufferLifespan(long second);
    void onUpdateScanningTimes(JSONObject scanResult);
}
