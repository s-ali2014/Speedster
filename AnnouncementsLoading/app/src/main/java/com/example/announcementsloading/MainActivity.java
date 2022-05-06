<<<<<<< HEAD
//Speedster Audible Speedometer
//Version 1
=======
>>>>>>> Get-Speed
//Anna Langston
//Sabran
//Akram Hawsawi
//Ryan Narongvate


package com.example.announcementsloading;

<<<<<<< HEAD
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
=======

import android.Manifest;
>>>>>>> Get-Speed
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
<<<<<<< HEAD
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
=======
import android.content.pm.PackageManager;
import android.os.Build;
>>>>>>> Get-Speed
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
<<<<<<< HEAD

import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;


import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

=======
>>>>>>> Get-Speed

import java.util.List;
import java.util.Locale;

<<<<<<< HEAD
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    /*===Variables====*/
    /*Announcement & Settings:*/

    /*---Load Settings---*/
    float maxAnnounceThreshold = 100;
    float minAnnounceThreshold = 0;
    int announceInterval = 10;
    boolean useTTS = true;
    boolean maxSpeedWarning = false;

    /*---Announcement Variables---*/
=======
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
   // public String val="Speed Declaration";
    private final String[] locationsPermission = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private final int LOCATIONS_PERMISSION_REQUEST = 100;
    private BroadcastReceiver locationReceiver;
    private SpeedReader speedReader;

    MockBluetooth bluetooth = new MockBluetooth("speed");
    TextView textView2;
    //String dummyText = val;
    String dummyText = bluetooth.getSpeed();

    //GLOBAL-ENOUGH VARIABLES
>>>>>>> Get-Speed
    TextToSpeech tts;
    MutableLiveData<String> announceText = new MutableLiveData<String>();
    MutableLiveData<Integer> speed = new MutableLiveData<>();

    int previousAnnouncement;
    int announceCooldown = 5;
    boolean onCooldown = false;

    boolean enable_bt = true;//bluetooth code
    private BluetoothAdapter BA;

    TabLayout tablayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announceText.setValue(""); //Initialize announceText

        /*--------------Tab System--------------*/

        setContentView(R.layout.activity_main);


        tablayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager2);
        FragmentManager frag_man = getSupportFragmentManager();
        adapter = new FragmentAdapter(frag_man, getLifecycle());
        pager2.setAdapter(adapter);
        textView2 = (TextView)findViewById(R.id.textView2);

        tablayout.addTab(tablayout.newTab().setText("Overview"));
        tablayout.addTab(tablayout.newTab().setText("SETTINGS"));

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tablayout.selectTab(tablayout.getTabAt(position));
            }
        });



        /*--------------UI Interactive Elements--------------*/
        FloatingActionButton fab = findViewById(R.id.fab);
<<<<<<< HEAD
        RangeSlider speedRange = findViewById(R.id.speedRange);

        /*Range slider for announcements of speeds*/




        /*Button to be used for misc. debugging purposes.*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                announceText.setValue("0"); //Play audio to test tts
                Snackbar.make(view, "Min Thresh:" + minAnnounceThreshold + "Max Thresh" + maxAnnounceThreshold, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); // dialogue used for debuggging, edit as needed
            }
        });

        /*--------------Announcement System: Initialization--------------*/
        /*This right here is our good 'ol announcement system.*/

        tts = new TextToSpeech(MainActivity.this, this);

        /*Cooldown Timer*/
        CountDownTimer cooldown = new CountDownTimer(announceCooldown * 1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                onCooldown = false;
            }
        };



        /*--------------Announcement System--------------*/

        speed.observe(this, new Observer<Integer>() {
            //Okay, So here's the thing. This lets us listen to this string and do things when it changes.
            //To use it's data, use announceText.getValue(); or you'll get an error! Also use announceText.SetValue("value");
            @Override
            public void onChanged(Integer s) {
                /*WARNING: This is untested!*/
                if ((speed.getValue() / announceInterval) != previousAnnouncement) {
                    //TODO: Tone announcement
                    announceText.setValue(Integer.toString(speed.getValue()));
                    onCooldown = true;
                    cooldown.start();
                }
            }
        });

=======
        // getSpeed = findViewById(R.id.getSpeed);


        //ANNOUNCEMENT SYSTEM:
        tts = new TextToSpeech(MainActivity.this, this);
>>>>>>> Get-Speed
        announceText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                speakText();
            }
        });


<<<<<<< HEAD
        /*--------------Settings: Loading--------------*/

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        maxAnnounceThreshold = preferences.getFloat("maxAnnounceThreshold", 100);
        minAnnounceThreshold = preferences.getFloat("minAnnounceThreshold", 0);
        announceInterval = preferences.getInt("announceInterval", 10);
        useTTS = preferences.getBoolean("useTTS", true);
        maxSpeedWarning = preferences.getBoolean("maxSpeedWarning", false);


        //*------Bluetooth ------*//
        IntentFilter filter = new IntentFilter();                  //
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);   //Listening for bluetooth device to connect
        this.registerReceiver(mReceiver, filter);                //

        BA = BluetoothAdapter.getDefaultAdapter();  //199-205: Testing Bluetooth Compatibility on device
        if (BA == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            //finish();
        } else if (BA.isEnabled()) {
            enable_bt = true;
        }

        if (!enable_bt) { //if bluetooth is disable turn off bluetooth

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {

                BA.disable();
            }

            Toast.makeText(MainActivity.this, "Turned Off", Toast.LENGTH_SHORT).show();
        } else { //if bluetooth is enabled, ask for bluetooth permission
            Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentOn, 0);
            Toast.makeText(MainActivity.this, "Turned On", Toast.LENGTH_SHORT).show();
        }
    }



    //*----End of OnCreate----*//
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) { //callback function when Bluetooth device is connected
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) { //says when device is connected
                Toast.makeText(MainActivity.this, "Device Connected", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //*------End of Bluetooth ------*//





    /*--------------TTS System--------------*/
    /*TTS Initialization - WARNING: THIS DOES *NOT* WORK ON EMULATORS. IT WILL FAIL TO INITIALIZE EVERY TIME WITH A GENERIC ERROR CODE*/

=======
        /*getSpeed.setOnClickListener(new View.OnClickListener() {
            @override
            public void onClick(View view) {
                String speedLiteral;
                int speed;
                speedReader = new SpeedReader("010D");
                speed = speedReader.getSpeed();
                speedLiteral = speed + "";
                announceText.observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String speedLiteral) {
                        speakText();
                    }
                });
            }
        });*/

        /*//BUTTON. USE THIS FOR DEBUGGING FOR NOW.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                announceText.setValue("Your speed is: 0 miles per hour.");
                Snackbar.make(view, "Hello World!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // String speedLiteral;

                //int speed;
                //speedReader = new SpeedReader("010D");
                //speed = speedReader.getSpeed();


               // System.out.println("Debugging Speed: " +speed);
                //speedLiteral = speed + "";

                announceText.setValue("Your speed is: 0 miles per hour.");
<<<<<<< Updated upstream:AnnouncementsLoading/app/src/main/java/com/example/announcementsloading/MainActivity.java
                Snackbar.make(view, "Hello World!", Snackbar.LENGTH_LONG)
=======
                Snackbar.make(view,"Your speed is " + getSpeed + " mph", Snackbar.LENGTH_LONG)
>>>>>>> Stashed changes:app/src/main/java/com/example/announcementsloading/MainActivity.java
                        .setAction("Action", null).show();



            }
        });


        checkLocationPermission();
        initLocationReceiver();
    }


    //TTS Initialization - WARNING: THIS DOES *NOT* WORK ON EMULATORS. IT WILL FAIL TO INITIALIZE EVERY TIME WITH A GENERIC ERROR CODE
>>>>>>> Get-Speed
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            }
<<<<<<< HEAD
        } else if (status == -1){Log.e("DON'T USE EMULATOR", "TTS WONT WORK FOR SOME REASON. USE PHYSICAL DEVICE INSTEAD. If not using emulator this is a generic error");}
        else {
=======
        } else if (status == -1) {
            Log.e("DON'T USE EMULATOR", "TTS WONT WORK FOR SOME REASON. USE PHYSICAL DEVICE INSTEAD.");
        } else {
>>>>>>> Get-Speed
            Log.e("error code", Integer.toString(status));
        }
    }

<<<<<<< HEAD
    public void speakText(){
        /*Speaks text from the announceText variable.*/
        tts.speak(announceText.getValue(), TextToSpeech.QUEUE_FLUSH, null , "");
    }

    public void speakText(CharSequence toSpeak){
=======
    //Speaker. By default it takes no parameters and says announceText.
    public void speakText() {
        /*Speaks text from the announceText variable*/
        tts.speak(announceText.getValue(), TextToSpeech.QUEUE_FLUSH, null, "");
    }

    public void speakText(CharSequence toSpeak) {
>>>>>>> Get-Speed
        /*Speaks text from the given CharSequence. NOTE: Strings are char sequences*/
        tts.speak(toSpeak, 1, null, "test");
    }



    /*--------------Application Shutdown--------------*/

    protected void onDestroy() {
        /*--------------Save Settings--------------*/
        /*Saves all current settings to the app's preferences file. Might be worth looking into if this should be done onPause as well/instead*/
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("maxAnnounceThreshold", maxAnnounceThreshold);
        editor.putFloat("minAnnounceThreshold", minAnnounceThreshold);
        editor.putInt("announceInterval", announceInterval);
        editor.putInt("announceCooldown", announceCooldown);
        editor.putBoolean("useTTS", useTTS);
        editor.putBoolean("maxSpeedWarning", maxSpeedWarning);
        editor.commit();

        /*--------------Shutdowns--------------*/

        tts.shutdown();

        super.onDestroy();
    }


    private void checkLocationPermission() {
        if (checkPermission(locationsPermission[0])
                && checkPermission(locationsPermission[1])) {
            startLocationService();
        } else {
            askForPermissions();
        }
    }

    private void askForPermissions() {
        ActivityCompat.requestPermissions(this, locationsPermission, LOCATIONS_PERMISSION_REQUEST);
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, MyLocationService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(serviceIntent);
        } else {
            // Pre-O behavior.
            startService(serviceIntent);
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATIONS_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                showLocationDeniedDialog();
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showLocationDeniedDialog() {
        //TODO show dialog that describe to user how denying location permission will affect the app functionality
    }

    private void initLocationReceiver() {
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(MyLocationService.NEW_SPEED_RECORDED_ACTION)) {
                    int speed = intent.getIntExtra(MyLocationService.SPEED_TAG, 0);
                    //TODO do what you need todo in the new speed
                    Toast.makeText(MainActivity.this, "Speed: " + speed, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(locationReceiver, new IntentFilter(MyLocationService.NEW_SPEED_RECORDED_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationReceiver);
    }

    //Notes from Anna:
    //Announcement System:
    //TODO: Implement handling for onPause/background process things
    //TODO: Find a way to implement settings for switching data listened to
    //TODO: Implement custom announcement settings
    //TODO: Tracking speed- another mutable live data?
    //UI:
    //TODO: Set up speed display for general tab
    //Data reading:
    //TODO: Implement GPS
    //TODO: Implement Bluetooth OBD2 info
}

