//Speedster Audible Speedometer
//Version 1
//Anna Langston
//Sabran
//Akram Hawsawi
//Ryan Narongvate

package com.example.announcementsloading;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.View;

import android.widget.Toast;


import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    /*===Variables====*/
    /*Announcement & Settings:*/



    /*---Load Settings---*/
//See commit description

    /*---Announcement Variables---*/
    TextToSpeech tts;
    MutableLiveData<String> announceText = new MutableLiveData<>();


    int previousAnnouncement;
    boolean onCooldown = false;

    boolean use_bt = true;//True if We want to use Bluetooth
    boolean bt_enabled = false;//True if Bluetooth is Enabled

    private BluetoothAdapter BA;

    TabLayout tablayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announceText.setValue(""); //Initialize announceText




        //Initialize speed.
        OverviewViewModel.speed.setValue(0);

        /*--------------Tab System--------------*/

        setContentView(R.layout.activity_main);


        tablayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager2);
        FragmentManager frag_man = getSupportFragmentManager();
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

        /*--------------Settings: Loading--------------*/

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        SettingsViewModel.maxAnnounceThreshold = preferences.getFloat("maxAnnounceThreshold", 100);
        SettingsViewModel.minAnnounceThreshold = preferences.getFloat("minAnnounceThreshold", 0);
        SettingsViewModel.announceInterval = preferences.getFloat("announceInterval", 10);
        SettingsViewModel.useTTS = preferences.getBoolean("useTTS", true);
        SettingsViewModel.maxSpeedWarning = preferences.getBoolean("maxSpeedWarning", false);


        /*--------------UI Interactive Elements--------------*/
        FloatingActionButton fab = findViewById(R.id.fab);



        /*Button to be used for misc. debugging purposes.*/
        fab.setOnClickListener(view -> {
            OverviewViewModel.speed.setValue(OverviewViewModel.speed.getValue()+3);


            Snackbar.make(view, "interval: " + preferences.getFloat("announceInterval", 1), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show(); // dialogue used for debugging, edit as needed
        });



        /*--------------Announcement System--------------*/
        /*Anna Langston. Requires API Level 21+*/

        /*---Announcement System: Initialization---*/
        /*This right here is our good 'ol announcement system.*/

        tts = new TextToSpeech(MainActivity.this, this);

        /*Cooldown Timer*/
        CountDownTimer cooldown = new CountDownTimer(SettingsViewModel.announceCooldown * 1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                onCooldown = false;
            }
        };


        /*---Announcement System: Tone---*/
        //NOTE: This requires API Level 21. That's lollipop (5.0) though so it should not be considered an issue.
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        SoundPool tone = new SoundPool.Builder().setAudioAttributes(attributes).build();
        int toneId = tone.load(this,R.raw.coin_2,1);


        /*---Announcement System: Processing---*/
        OverviewViewModel.speed.observe(this, new Observer<Integer>() {
            //Observes speed value for changes, using those changes to do announcements.
            @Override
            public void onChanged(Integer s) {
                //Handles announcement frequency here
                int currentInterval = (int) (OverviewViewModel.speed.getValue() / SettingsViewModel.announceInterval);

                if (!onCooldown && currentInterval != previousAnnouncement) {
                    if(SettingsViewModel.useTTS) {
                        //TTS Announcement
                       announceText.setValue(Integer.toString(OverviewViewModel.speed.getValue()));
                    }
                    else{
                        //Tone Announcement
                        tone.play(toneId, 1,1,0,0,1);
                    }
                    previousAnnouncement = currentInterval;
                    onCooldown = true;
                    cooldown.start();
                }
            }
        });

        announceText.observe(this, s -> speakText());




        //*--------------Bluetooth--------------*//

        IntentFilter filter = new IntentFilter();                  //
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);   //Listening for bluetooth device to connect
        this.registerReceiver(mReceiver, filter);                //

        BA = BluetoothAdapter.getDefaultAdapter();  //199-205: Testing Bluetooth Compatibility on device
        if (BA == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            //finish();
        } else if (BA.isEnabled()) {
            bt_enabled = true;
        }

        if (use_bt) { //if we want to use Bluetooth
            if (!bt_enabled){ //If Bluetooth is disabled, ask to enable Bluetooth
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { //Ask for Bluetooth permission if not already given
                    Toast.makeText(MainActivity.this, "Debugging", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                } else{
                    Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intentOn, 0);
                    bt_enabled = true;
                }
            }
        }
    }

    //*----End of OnCreate----*//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults){
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            try {
                Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentOn, 0);
                bt_enabled = true;
            } catch (SecurityException e){
                throw e;
            }
        }

    }


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

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            }
        } else if (status == -1){Log.e("DON'T USE EMULATOR", "TTS WONT WORK FOR SOME REASON. USE PHYSICAL DEVICE INSTEAD. If not using emulator this is a generic error");}
        else {
            Log.e("error code", Integer.toString(status));
        }
    }

    public void speakText(){
        /*Speaks text from the announceText variable.*/
        tts.speak(announceText.getValue(), TextToSpeech.QUEUE_FLUSH, null , "");
    }

    public void speakText(CharSequence toSpeak){
        /*Speaks text from the given CharSequence. NOTE: Strings are char sequences*/
        tts.speak(toSpeak, 1, null , "test");
    }



    /*--------------Application Shutdown--------------*/

    protected void onDestroy() {
        /*--------------Save Settings--------------*/
        /*Saves all current settings to the app's preferences file. Might be worth looking into if this should be done onPause as well/instead*/
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("maxAnnounceThreshold", SettingsViewModel.maxAnnounceThreshold);
        editor.putFloat("minAnnounceThreshold", SettingsViewModel.minAnnounceThreshold);
        editor.putFloat("announceInterval", SettingsViewModel.announceInterval);
        editor.putInt("announceCooldown", SettingsViewModel.announceCooldown);
        editor.putBoolean("useTTS", SettingsViewModel.useTTS);
        editor.putBoolean("maxSpeedWarning", SettingsViewModel.maxSpeedWarning);
        editor.apply();

        /*--------------Shutdowns--------------*/

        tts.shutdown();

        super.onDestroy();
    }


    //Notes from Anna:
    //Announcement System:
    /*TODO: All done!*/
    
    //Data reading:
    //TODO: Implement GPS
    //TODO: Implement Bluetooth OBD2 info
}
