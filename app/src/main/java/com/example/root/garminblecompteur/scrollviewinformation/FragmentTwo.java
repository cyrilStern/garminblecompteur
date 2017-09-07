package com.example.root.garminblecompteur.scrollviewinformation;

/**
 * Created by cyrilstern1 on 07/09/2017.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.garminblecompteur.R;

import org.osmdroid.views.MapView;

public class FragmentTwo extends Fragment {
    private String title;
    private int image;

    public static FragmentTwo newInstance(String title) {
        FragmentTwo fragment = new FragmentTwo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        MapView map = (MapView) view.findViewById(R.id.map);
        
        return view;
    }
}