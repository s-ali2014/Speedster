package com.example.announcementsloading;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Overview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Overview extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private BroadcastReceiver locationReceiver;

    private EditText etSpeed;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Overview() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Overview.
     */
    // TODO: Rename and change types and number of parameters
    public static Overview newInstance(String param1, String param2) {
        Overview fragment = new Overview();
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
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etSpeed = view.findViewById(R.id.editTextNumber3);
        initLocationReceiver();
    }

    private void initLocationReceiver() {
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(MyLocationService.NEW_SPEED_RECORDED_ACTION)) {
                    float speed = intent.getFloatExtra(MyLocationService.SPEED_TAG, 0F);
                    String speedStr = speed + "";
                    if (etSpeed != null)
                        etSpeed.setText(speedStr);
                    Toast.makeText(requireContext(), "Speed: " + speed, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        requireContext().registerReceiver(locationReceiver, new IntentFilter(MyLocationService.NEW_SPEED_RECORDED_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(locationReceiver);
    }

}