package com.example.pond.wifiscanner;

import android.net.wifi.ScanResult;

import java.util.List;

public class TimeStampScanResults {

    private long timeStamp;
    private List<ScanResult> scanResults;

    public TimeStampScanResults(long timeStamp, List<ScanResult> scanResults) {
        this.timeStamp = timeStamp;
        this.scanResults = scanResults;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public List<ScanResult> getScanResults() {
        return this.scanResults;
    }

    public int size() {
        return this.scanResults.size();
    }

}
