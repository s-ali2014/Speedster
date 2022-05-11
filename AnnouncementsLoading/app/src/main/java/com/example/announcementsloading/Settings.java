//Anna Langston
package com.example.announcementsloading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);








        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        SettingsViewModel model = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        /*-------UI ELEMENTS------*/
        /*---Declarations---*/
        Slider announceFreq = view.findViewById(R.id.intervalSlider);
        Slider maxSpeed = view.findViewById(R.id.maxSpeedWarnSlider);
        RangeSlider speedRange = view.findViewById(R.id.speedRange);
        SwitchMaterial maxSpeedWarn = view.findViewById(R.id.maxSpeedWarn);
        SwitchMaterial useTTS = view.findViewById(R.id.switchTTS);
        Slider cooldown = view.findViewById(R.id.cooldownSlider);

        /*---INITIALIZATION---*/
        speedRange.setValues(model.minAnnounceThreshold, model.maxAnnounceThreshold);
        maxSpeed.setValue(model.warnSpeed);
        cooldown.setValue(model.announceCooldown);
        useTTS.setChecked(model.useTTS);
        maxSpeedWarn.setChecked(model.maxSpeedWarning);
        announceFreq.setValue(model.announceInterval);

    /*USE TTS SWITCH*/
    useTTS.setOnCheckedChangeListener((compoundButton, b) -> model.useTTS = useTTS.isChecked()
    );
    /*MAX SPEED WARN SWITCH*/
        maxSpeedWarn.setOnCheckedChangeListener((compoundButton, b) -> model.maxSpeedWarning = maxSpeedWarn.isChecked()
        );

    /*COOLDOWN SLIDER*/
        cooldown.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider cooldown) { }
            @Override
            public void onStopTrackingTouch(@NonNull Slider cooldown) {
                model.announceCooldown = (int) cooldown.getValue();
            }
        });

    /*---WARN SPEED SLIDER---*/
        maxSpeed.addOnSliderTouchListener(new Slider.OnSliderTouchListener(){
            @Override
            public void onStartTrackingTouch(@NonNull Slider announceFreq){}
            public void onStopTrackingTouch(@NonNull Slider announceFreq){
                model.warnSpeed = maxSpeed.getValue();
            }

        });
    /*---INTERVAL SLIDER---*/
        announceFreq.addOnSliderTouchListener(new Slider.OnSliderTouchListener(){
            @Override
            public void onStartTrackingTouch(@NonNull Slider announceFreq){}
            public void onStopTrackingTouch(@NonNull Slider announceFreq){
                model.announceInterval = announceFreq.getValue();
            }
        });


    /*---ANNOUNCE RANGE SLIDER---*/
        speedRange.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider speedRange) {}

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider speedRange) {
                List<Float> rangeValues = speedRange.getValues();
                model.minAnnounceThreshold = rangeValues.get(0);
                model.maxAnnounceThreshold = rangeValues.get(1);
            }
        });

        return view;
    }
}