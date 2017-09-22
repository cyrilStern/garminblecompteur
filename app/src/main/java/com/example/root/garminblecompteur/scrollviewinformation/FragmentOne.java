package com.example.root.garminblecompteur.scrollviewinformation;

/**
 * Created by cyrilstern1 on 07/09/2017.
 */


import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.garminblecompteur.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.Random;

public class FragmentOne extends Fragment {
    public static MapView mapView;
    private String title;
    private int image;
    private Marker currentMarker;
    private Marker positionUser;


    public static FragmentOne newInstance(String title) {
        FragmentOne fragment = new FragmentOne();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * Load Map
         * Map created for offline with MOBAC
         */
        String atlasName = "Google Maps";
        String atlasExtension = ".png";
        int tileSizePixels = 500;
        float defaultLatitude = 48.858093f;
        float defaultLongitude = 2.294694f;
        int minZoom = 1;
        int maxZoom = 16;
        int defaultZoom = 5;
        mapView.setTileSource(new XYTileSource(atlasName, minZoom, maxZoom, tileSizePixels, atlasExtension, new String[] {}));
        mapView.setBuiltInZoomControls(true);
        mapView.setBackgroundColor(Color.BLACK);
        mapView.getController().setZoom(defaultZoom);
        mapView.setClickable(true);
        mapView.getController().setCenter(new GeoPoint((int)(defaultLatitude * 1E6), (int)(defaultLongitude * 1E6)));
        positionUser = new Marker(mapView,getActivity().getApplicationContext());
        positionUser.setIcon(getActivity().getDrawable(R.drawable.arrow_direction));
        positionUser.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        positionUser.setInfoWindow(new MarkerInfoWindow(R.layout.marker_info_layout,mapView));

        /**
         * optional, but a good way to prevent loading from the network and test your zip loading.
         *
         */
        mapView.setUseDataConnection(false);

        IMapController mapController = mapView.getController();
        mapController.setZoom(5);
        mapView.invalidate();

    }

    public View getmapView(){
        return mapView;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);

        /**
         * MapCreationBlock
         */
        mapView = (MapView) view.findViewById(R.id.map);


        return view;
    }

    public void setPositionMarker(Location location){
        if (mapView != null) {
            positionUser.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
            positionUser.setPanToView(true);
            mapView.getOverlays().add(positionUser);
            mapView.invalidate();
        }
    }

    public void setTrace(Polyline line) {
        /**clear Map before add.**/
        mapView.getOverlays().clear();
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        line.setColor(color);
        Log.i("viewholder", "onClick: " + line.getTitle());
        mapView.getOverlayManager().add(line);
        mapView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.destroyDrawingCache();

    }
}