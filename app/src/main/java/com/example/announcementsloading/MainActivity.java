//Anna Langston
//Sabran
//[Add your name here when you add some code!]


package com.example.announcementsloading;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    public String val="Speed Declaration";
    private final String[] locationsPermission = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private final int LOCATIONS_PERMISSION_REQUEST = 100;
    private BroadcastReceiver locationReceiver;
    private SpeedReader speedReader;

    MockBluetooth bluetooth = new MockBluetooth("speed");
    TextView textView2;

    String getSpeed = bluetooth.getSpeed();

    //GLOBAL-ENOUGH VARIABLES
    TextToSpeech tts;
    MutableLiveData<String> announceText = new MutableLiveData<String>();

    TabLayout tablayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;
    //Okay, So here's the thing. This lets us listen to this string and do things when it changes.
    //To use it's data, use announceText.getValue(); or you'll get an error! Also use announceText.SetValue("value");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announceText.setValue(""); //Initialize announceText

        //Tab System
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

//i hate this button now - Anna
        FloatingActionButton fab = findViewById(R.id.fab);
        // getSpeed = findViewById(R.id.getSpeed);


        //ANNOUNCEMENT SYSTEM:
        tts = new TextToSpeech(MainActivity.this, this);
        announceText.observe(this, new Observer<String>() {
            /*This right here is our good 'ol announcement system. It's bare-bones and unrefined, but essentially, when announceText changes?
            speakText() is called to announce the new value.
            */
            @Override
            public void onChanged(String s) {
                speakText();
            }
        });


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
                String speedLiteral;

                int speed;
                speedReader = new SpeedReader("010D");
                speed = speedReader.getSpeed();


               // System.out.println("Debugging Speed: " +speed);
                speedLiteral = speed + "";

                announceText.setValue("Your speed is: 0 miles per hour.");
                Snackbar.make(view,"Your speed is " +getSpeed+ " mph", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();



            }
        });


        checkLocationPermission();
        initLocationReceiver();
    }


    //TTS Initialization - WARNING: THIS DOES *NOT* WORK ON EMULATORS. IT WILL FAIL TO INITIALIZE EVERY TIME WITH A GENERIC ERROR CODE
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            }
        } else if (status == -1) {
            Log.e("DON'T USE EMULATOR", "TTS WONT WORK FOR SOME REASON. USE PHYSICAL DEVICE INSTEAD.");
        } else {
            Log.e("error code", Integer.toString(status));
        }
    }

    //Speaker. By default it takes no parameters and says announceText.
    public void speakText() {
        /*Speaks text from the announceText variable*/
        tts.speak(announceText.getValue(), TextToSpeech.QUEUE_FLUSH, null, "");
    }

    public void speakText(CharSequence toSpeak) {
        /*Speaks text from the given CharSequence. NOTE: Strings are char sequences*/
        tts.speak(toSpeak, 1, null, "test");
    }

    //Release the TTS service when the app is destroyed
    protected void onDestroy() {
        tts.shutdown(); //This is the end right?
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
    //TODO: Listen for speed variable
    //TODO: Implement handling for onPause/background process things
    //TODO: Overload speakText
    //TODO: Find a way to implement settings for switching data listened to
    //TODO: Implement custom announcement settings
    //UI:
    //TODO: Set up dummy UI for prototype
    //TODO: Set up speed display for general tab
    //Data reading:
    //TODO: Implement GPS
    //TODO: Implement Bluetooth OBD2 info
}

