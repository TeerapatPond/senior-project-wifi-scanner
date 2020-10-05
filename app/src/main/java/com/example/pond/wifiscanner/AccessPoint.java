package com.example.pond.wifiscanner;

public class AccessPoint {
    private String BSSID;
    private String SSID;
    private int RSSI;
    private int frequency;

    public AccessPoint(String BSSID, String SSID, int RSSI, int frequency) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.RSSI = RSSI;
        this.frequency = frequency;
    }

    public String getBSSID() {
        return this.BSSID;
    }

    public String getSSID() {
        return this.SSID;
    }

    public int getRSSI() {
        return this.RSSI;
    }

    public int getFrequency() {
        return this.frequency;
    }
}

