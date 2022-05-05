//Speedster Audible Speedometer
//Version 0
//Anna Langston
//Sabran
//Ryan Narongvate
//[Add your name here when you add some code!]

package com.example.announcementsloading;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import android.speech.tts.TextToSpeech;


import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.View;

import android.bluetooth.BluetoothAdapter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

import java.util.Locale;

//Bluetooth
import android.bluetooth.BluetoothAdapter;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    //Variables for TTS and Announcement handling
    TextToSpeech tts;
    MutableLiveData<String> announceText = new MutableLiveData<String>();
    int maxAnnounceThreshold = 100;
    int minAnnounceThreshold = 0;
    int announceInterval = 10;
    boolean useTTS = true;
    boolean maxSpeedWarning = false;
    int previousAnnouncement;
    int announceCooldown = 5;

    //Bluetooth Variable
    CheckBox enable_bt; //need to add button
    private BluetoothAdapter BA;

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


        tablayout=findViewById(R.id.tab_layout);
        pager2=findViewById(R.id.view_pager2);
        FragmentManager frag_man=getSupportFragmentManager();
        adapter = new FragmentAdapter(frag_man, getLifecycle());
        pager2.setAdapter(adapter);

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
        RangeSlider speedRange = findViewById(R.id.speedRange);


        //ANNOUNCEMENT SYSTEM:
        tts=new TextToSpeech(MainActivity.this, this);
        announceText.observe(this, new Observer<String>() {
            /*This right here is our good 'ol announcement system. It's bare-bones and unrefined, but essentially, when announceText changes?
            speakText() is called to announce the new value.

            WIP: cooldown system!
            */
            @Override
            public void onChanged(String s) {
                //int currentInterval = Integer.valueOf(announceText.getValue()) / announceInterval;
                //if( currentInterval != previousAnnouncement){
                speakText();
                //}
            }
        });



        //BUTTON. USE THIS FOR DEBUGGING FOR NOW.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                announceText.setValue("Your speed is: 0 miles per hour.");
                Snackbar.make(view, "Hello World!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //*--------------------Bluetooth Implementation--------------------*//

    enable_bt = findViewById(R.id.enable_bt);
    IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(mReceiver, filter);

    BA = BluetoothAdapter.getDefaultAdapter();
        if (BA == null) {
        Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        finish();
    } else if (BA.isEnabled()) {
        enable_bt.setChecked(true);
    }
        enable_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (!isChecked) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    BA.disable();
                }

                Toast.makeText(MainActivity.this, "Turned Off", Toast.LENGTH_SHORT).show();
            } else {
                Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentOn, 0);
                Toast.makeText(MainActivity.this, "Turned On", Toast.LENGTH_SHORT).show();
            }
        }
    });

}


    //*----End of OnCreate----*//
private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
        Toast.makeText(MainActivity.this, "Device Connected", Toast.LENGTH_SHORT).show();
        }
    }
};




    /*--------------TTS System--------------*/
    /*TTS Initialization - WARNING: THIS DOES *NOT* WORK ON EMULATORS. IT WILL FAIL TO INITIALIZE EVERY TIME WITH A GENERIC ERROR CODE*/


    //TTS Initialization - WARNING: THIS DOES *NOT* WORK ON EMULATORS. IT WILL FAIL TO INITIALIZE EVERY TIME WITH A GENERIC ERROR CODE
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            }
        } else if (status == -1){Log.e("DON'T USE EMULATOR", "TTS WONT WORK FOR SOME REASON. USE PHYSICAL DEVICE INSTEAD.");}
        else {
            Log.e("error code", Integer.toString(status));
        }
    }

    //Speaker. By default it takes no parameters and says announceText.
    public void speakText(){
        /*Speaks text from the announceText variable, and resets the counter since last announce.*/
        tts.speak(announceText.getValue(), TextToSpeech.QUEUE_FLUSH, null , "");

    }
    public void speakText(CharSequence toSpeak){
        /*Speaks text from the given CharSequence. NOTE: Strings are char sequences*/
        tts.speak(toSpeak, 1, null , "test");
    }

    //Release the TTS service when the app is destroyed
    protected void onDestroy() {
        tts.shutdown(); //This is the end right?
        super.onDestroy();
    }


    //Notes from Anna:
    //Announcement System:
    //TODO: Implement handling for onPause/background process things
    //TODO: Overload speakText
    //TODO: Find a way to implement settings for switching data listened to
    //TODO: Implement custom announcement settings
    //UI:
    //TODO: Set up speed display for general tab
    //Data reading:
    //TODO: Implement GPS
    //TODO: Implement Bluetooth OBD2 info

}
