package com.example.announcementsloading;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    public static float maxAnnounceThreshold = 100;
    public static float minAnnounceThreshold = 0;
    public static float announceInterval = 10;
    public static boolean useTTS = true;
    public static boolean maxSpeedWarning = false;
    public static int announceCooldown = 5;
}
