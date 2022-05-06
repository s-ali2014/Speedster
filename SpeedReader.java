package com.example.announcementsloading;

public class SpeedReader {

    private String speedStr;
    public SpeedReader(String speedCode) {


        //When the Bluetooth class is ready then this code can work
        //Bluetooth bluetooth = new Bluetooth(String obd2Code);
        //bluetooth.speed = bluetooth.getSpeed();



        // This is Mock Class to call the Bluetooth class
        MockBluetooth mockBluetooth = new MockBluetooth("010D");
        mockBluetooth.speed = mockBluetooth.getSpeed();
    }

    public int getSpeed() {
        return Integer.parseInt(speedStr, 16);
    }
}