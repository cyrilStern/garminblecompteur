package com.example.root.garminblecompteur.scrollviewinformation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.garminblecompteur.R;

import java.util.ArrayList;

/**
 * Created by cyrilstern1 on 18/09/2017.
 */

public class CardTraceAdapteur extends RecyclerView.Adapter<CardTraceAdapteur.ViewHolder> {

    private ArrayList<String> mDataset;

    public CardTraceAdapteur(ArrayList<String> mDataset) {
        this.mDataset = mDataset;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.textView);
            mImageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }
    @Override
    public CardTraceAdapteur.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liste_trace_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CardTraceAdapteur.ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position));


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
