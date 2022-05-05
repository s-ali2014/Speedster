package com.example.announcementsloading

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.google.android.gms.location.*
import kotlin.math.roundToInt

class MyLocationService : Service() {

    //Ten minutes
    private val UPDATES_INTERVAL = 1 * 60 *  1000L
    private val MILES_INTERVAL = 5

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initLocationCallback()
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }


        val notification: Notification =
            NotificationCompat.Builder(this, LOCATION_FOREGROUND_SERVICE_CHANNEL_ID)
                .setOngoing(true)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentTitle("Location Service")
                .setContentText("Location Service is running")
                .setContentIntent(pendingIntent)
                .build()


        startForeground(ONGOING_NOTIFICATION_ID, notification)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun initLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    onNewLocation(location)
                }
            }
        }
    }


    private var firstLocation: Location? = null
    private var lastLocation: Location? = null

    private val speeds = mutableListOf<Float>()
    private var lastTime = System.currentTimeMillis()

    @Synchronized
    private fun onNewLocation(location: Location) {
        lastLocation = location
        val currentTime = System.currentTimeMillis()
        Log.i(TAG, "CurrentTime: $currentTime")

        Log.i(TAG, "locations size: $lastLocation")
        Log.i(TAG, "speeds size: ${speeds.size}")
        Log.i(TAG, "onNewLocation: ${location.latitude},${location.longitude}")

        if (firstLocation == null) {
            firstLocation = location
            lastTime = currentTime
            Log.i(TAG, "lastTime: $currentTime")
            return
        }

        val timePeriodInMinutes = (currentTime - lastTime) / (1000 * 60)
        if (timePeriodInMinutes <= 0L) {
            return
        }

        Log.i(TAG, "timePeriodInMinutes: $timePeriodInMinutes")
        val lastDistanceTraveledInMiles = calculateLastDistanceTraveled(location)
        Log.i(TAG, "lastDistanceTraveledInMiles: $lastDistanceTraveledInMiles")
        val totalDistanceTraveledInMiles = calculateTotalDistanceTraveled(location)
        Log.i(TAG, "totalDistanceTraveledInMiles: $totalDistanceTraveledInMiles")
        val lastCalculatedSpeedMilePerMinute = lastDistanceTraveledInMiles / timePeriodInMinutes
        Log.i(TAG, "lastCalculatedSpeedMilePerMinute: $lastCalculatedSpeedMilePerMinute")
        speeds.add(lastCalculatedSpeedMilePerMinute)

        if (totalDistanceTraveledInMiles >= MILES_INTERVAL) {
            Log.i(TAG, "speeds: $speeds")
            val averageSpeed = calculateAverageSpeed()
            Log.i(TAG, "averageSpeed: $averageSpeed")
            resetSavedData()
            sendAverageSpeed(averageSpeed)
        }

        lastTime = currentTime
    }

    private fun sendAverageSpeed(averageSpeed: Float) {
        sendBroadcast(Intent(NEW_SPEED_RECORDED_ACTION).apply {
            putExtra(SPEED_TAG, averageSpeed.roundToInt())
        })
    }

    private fun resetSavedData() {
        firstLocation = lastLocation
        speeds.clear()
    }

    private fun calculateAverageSpeed(): Float {
        return (speeds.sum() / speeds.count()) / 60 //To convert it per hour
    }


    private fun calculateLastDistanceTraveled(location: Location): Float {
        return location.distanceTo(lastLocation).toMile()
    }

    private fun calculateTotalDistanceTraveled(location: Location): Float {
        return location.distanceTo(firstLocation).toMile()
    }

    private fun Float.toMile(): Float {
        return this * 0.000621F
    }


    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }
        fusedLocationClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = UPDATES_INTERVAL
            fastestInterval = UPDATES_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    companion object {

        private const val TAG = "MyLocationService"

        const val LOCATION_FOREGROUND_SERVICE_CHANNEL_ID = "locationService"
        const val LOCATION_FOREGROUND_SERVICE_CHANNEL_TITLE = "Location Service"
        const val LOCATION_FOREGROUND_SERVICE_CHANNEL_DESCRIPTION = "Location service"
        const val ONGOING_NOTIFICATION_ID = 101


        const val NEW_SPEED_RECORDED_ACTION = "newSpeedRecorded"
        const val SPEED_TAG = "speed"

    }

}