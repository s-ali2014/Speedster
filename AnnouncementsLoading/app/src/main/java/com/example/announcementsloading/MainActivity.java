//Speedster Audible Speedometer
//Version 1
//Anna Langston
//Sabran
//Akram Hawsawi

package com.example.announcementsloading;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;


import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.View;


import java.util.List;
import java.util.Locale;

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
    TextToSpeech tts;
    MutableLiveData<String> announceText = new MutableLiveData<String>();
    MutableLiveData<Integer> speed = new MutableLiveData<>();

    int previousAnnouncement;
    int announceCooldown = 5;
    boolean onCooldown = false;




    TabLayout tablayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announceText.setValue(""); //Initialize announceText

        /*--------------Tab System--------------*/

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

//Example

        /*--------------UI Interactive Elements--------------*/
        FloatingActionButton fab = findViewById(R.id.fab);
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

        tts=new TextToSpeech(MainActivity.this, this);

        /*Cooldown Timer*/
        CountDownTimer cooldown = new CountDownTimer(announceCooldown * 1000,1000) {
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
                if((speed.getValue() / announceInterval) != previousAnnouncement){
                    //TODO: Tone announcement
                    announceText.setValue(Integer.toString(speed.getValue()));
                    onCooldown = true;
                    cooldown.start();
                }
            }
        });

        announceText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                speakText();
            }
        });


        /*--------------Settings: Loading--------------*/

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        maxAnnounceThreshold = preferences.getFloat("maxAnnounceThreshold", 100);
        minAnnounceThreshold = preferences.getFloat("minAnnounceThreshold", 0);
        announceInterval = preferences.getInt("announceInterval", 10);
        useTTS = preferences.getBoolean("useTTS", true);
        maxSpeedWarning = preferences.getBoolean("maxSpeedWarning", false);
    }
    //*----End of OnCreate----*//





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
