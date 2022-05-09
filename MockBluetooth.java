package com.example.announcementsloading;
import java.util.Random;
public class MockBluetooth {
    String speed;
    Random random = new Random();
    public MockBluetooth(String obd2Code) {
        speed = Integer.toHexString(random.nextInt(110) + 25);
    }

    public String getSpeed() {
        return speed;
    }
}