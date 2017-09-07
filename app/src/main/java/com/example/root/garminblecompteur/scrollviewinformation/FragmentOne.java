package com.example.root.garminblecompteur.scrollviewinformation;

/**
 * Created by cyrilstern1 on 07/09/2017.
 */


        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.example.root.garminblecompteur.R;

        import org.osmdroid.tileprovider.tilesource.XYTileSource;
        import org.osmdroid.util.GeoPoint;
        import org.osmdroid.views.MapView;

public class FragmentOne extends Fragment {
    private String title;
    private int image;
    public static MapView mapview;


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
    public View getmapview(){
        return this.mapview;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);

        /**
         * Load Map
         * Map created for offline with MOBAC
         */
        String atlasName = "Google Map";
        String atlasExtension = ".png";
        int tileSizePixels = 500;
        float defaultLatitude = 48.858093f;
        float defaultLongitude = 2.294694f;
        int minZoom = 1;
        int maxZoom = 17;
        int defaultZoom = 10;

        /**
         * MapCreationBlock
         */
        mapview = (MapView) view.findViewById(R.id.map);
        mapview.setTileSource(new XYTileSource(atlasName, minZoom, maxZoom, tileSizePixels, atlasExtension, new String[] {}));
        mapview.setBuiltInZoomControls(true);
        mapview.setBackgroundColor(Color.BLACK);
        mapview.getController().setZoom(defaultZoom);
        mapview.setClickable(true);
        mapview.getController().setCenter(new GeoPoint((int)(defaultLatitude * 1E6), (int)(defaultLongitude * 1E6)));

        return view;
    }
}