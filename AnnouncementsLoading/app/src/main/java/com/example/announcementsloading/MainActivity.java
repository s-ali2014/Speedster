//Speedster Audible Speedometer
//Version 0
//Anna Langston
//Sabran
//[Add your name here when you add some code!]

package com.example.announcementsloading;

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


import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    //Variables
    TextToSpeech tts;
    MutableLiveData<String> announceText = new MutableLiveData<String>();
    MutableLiveData<Integer> speed = new MutableLiveData<>();
    int maxAnnounceThreshold = 100;
    int minAnnounceThreshold = 0;
    int announceInterval = 10;
    boolean useTTS = true;
    boolean maxSpeedWarning = false;
    int previousAnnouncement;
    int announceCooldown = 5;
    boolean onCooldown = false;

    TabLayout tablayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;
    //Okay, So here's the thing. This lets us listen to this string and do things when it changes.
    //To use it's data, use announceText.getValue(); or you'll get an error! Also use announceText.SetValue("value");

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


        /*--------------UI Interactive Elements--------------*/
        FloatingActionButton fab = findViewById(R.id.fab);
        RangeSlider speedRange = findViewById(R.id.speedRange);


        /*Button to be used for misc. debugging purposes.*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                announceText.setValue("0");
                Snackbar.make(view, "Hello World!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        /*--------------Announcement System: Initialization--------------*/
        /*This right here is our good 'ol announcement system. It's bare-bones and unrefined, but when the speed is changed, it checks if it's hit
         a new milestone based on the increment, and then if the cooldown isn't running, it changes announceText to trigger the announcement!
         TODO: Configure tone vs Speech settings.
        */

        tts=new TextToSpeech(MainActivity.this, this);
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
            @Override
            public void onChanged(Integer s) {
                /*WARNING: This is untested!*/
                if((speed.getValue() / announceInterval) != previousAnnouncement){
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
    }

    /*--------------TTS System--------------*/
    /*TTS Initialization - WARNING: THIS DOES *NOT* WORK ON EMULATORS. IT WILL FAIL TO INITIALIZE EVERY TIME WITH A GENERIC ERROR CODE*/



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
        tts.shutdown(); //This is the end, right?
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
