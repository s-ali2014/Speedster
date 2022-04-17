package com.example.announcementsloading;

public class SpeedReader {

    private String speedStr;
    public SpeedReader(String speedCode) {


        MockBluetooth mockBluetooth = new MockBluetooth("010D");
        mockBluetooth.speed = mockBluetooth.getSpeed();
    }

    public int getSpeed() {
        return Integer.parseInt(speedStr, 16);
    }
}