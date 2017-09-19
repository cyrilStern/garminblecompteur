package com.example.root.garminblecompteur.scrollviewinformation;

/**
 * Created by cyrilstern1 on 07/09/2017.
 */


import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.root.garminblecompteur.FileContainer;
import com.example.root.garminblecompteur.FileService;
import com.example.root.garminblecompteur.R;
import com.example.root.garminblecompteur.XmlToGeoJson;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

public class FragmentThree extends Fragment {
    private RecyclerView mRecyclerView;
    private CardTraceAdapteur mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> listNameGpsFile;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Parse SDCARD to get gpxTrace, and feed listview side memnu
         */
        FileService fileService = FileService.getInstance();
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(getContext(),"this is emulated device",Toast.LENGTH_SHORT).show();

        }else{
            final ArrayList<FileContainer> arraylistFileContainer = fileService.getListFile("gpstrace");
            listNameGpsFile = new ArrayList<>();
            for (FileContainer fileGps: arraylistFileContainer) {
                listNameGpsFile.add(fileGps.getName());
            }

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewTrace);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CardTraceAdapteur(listNameGpsFile);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}