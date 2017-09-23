package com.example.root.garminblecompteur.scrollviewinformation;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.garminblecompteur.FileContainer;
import com.example.root.garminblecompteur.MainActivity;
import com.example.root.garminblecompteur.R;
import com.example.root.garminblecompteur.XmlToGeoJson;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by cyrilstern1 on 18/09/2017.
 */

public class CardTraceAdapteur extends RecyclerView.Adapter<CardTraceAdapteur.ViewHolder> {

    private ArrayList<String> mDataset;
    private MainActivity mactivity;
    private ArrayList<FileContainer> marraylistFileContainer;

    public CardTraceAdapteur(ArrayList<String> mDataset, ArrayList<FileContainer> arraylistFileContainer, FragmentActivity activity) {
        this.mDataset = mDataset;
        this.mactivity = (MainActivity) activity;
        this.marraylistFileContainer = arraylistFileContainer;
    }

    @Override
    public CardTraceAdapteur.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liste_trace_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onBindViewHolder(CardTraceAdapteur.ViewHolder holder, final int position) {
        final ScreenSlidePagerAdapter mscreenSlidePagerAdapter = mactivity.getmPagerAdapter();
        holder.mTextView.setText(mDataset.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                XmlToGeoJson xmlToGeoJson = XmlToGeoJson.getInstance();
                /** Load GpsPoint from reading file.**/
                try {
                    ArrayList<GeoPoint> waypoints = xmlToGeoJson.decodeXmlToGeoJson(marraylistFileContainer.get(position).getPath(), mactivity.getApplicationContext());
                    Polyline line = new Polyline();
                    line.setTitle(mDataset.get(position));
                    line.setSubDescription(Polyline.class.getCanonicalName());
                    line.setWidth(10);
                    List<GeoPoint> pts = new ArrayList<>();
                    line.setPoints(waypoints);
                    line.setGeodesic(true);
                    //line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, map));

                    if (mscreenSlidePagerAdapter.getItem(0) instanceof FragmentOne) {
                        mactivity.getPager().setCurrentItem(0, true);
                        FragmentOne fm = ((FragmentOne) mscreenSlidePagerAdapter.getItem(0));
                        fm.setTrace(line);
                    }

                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        // each data item is just a string in this case
        private CardView cardView;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.cardview);
            mTextView = (TextView) v.findViewById(R.id.textView);
            mImageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}
