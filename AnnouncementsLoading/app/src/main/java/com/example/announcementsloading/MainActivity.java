//Speedster Audible Speedometer
//Version 0
//Anna Langston
//[Add your name here when you add some code!]

package com.example.announcementsloading;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import android.speech.tts.TextToSpeech;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.announcementsloading.ui.main.SectionsPagerAdapter;
import com.example.announcementsloading.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private ActivityMainBinding binding;
    //GLOBAL-ENOUGH VARIABLES
    TextToSpeech tts;
    MutableLiveData<String> announceText = new MutableLiveData<String>();
    //Okay, So here's the thing. This lets us listen to this string and do things when it changes.
    //To use it's data, use announceText.getValue(); or you'll get an error! Also use announceText.SetValue("value");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        announceText.setValue(""); //Initialize announceText
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        //ANNOUNCEMENT SYSTEM:
        tts=new TextToSpeech(MainActivity.this, this);
        announceText.observe(this, new Observer<String>() {
            /*This right here is our good 'ol announcement system. It's bare-bones and unrefined, but essentially, when announceText changes?
            speakText() is called to announce the new value.
             */
            @Override
            public void onChanged(String s) {
                speakText();
            }
        });



        //BUTTON. USE THIS FOR DEBUGGING FOR NOW.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                announceText.setValue("Yeet");
                Snackbar.make(view, "Hello World!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

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
        /*Speaks text from the announceText variable*/
        tts.speak(announceText.getValue(), 1, null , "test");
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
