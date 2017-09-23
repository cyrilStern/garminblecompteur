package com.example.root.garminblecompteur.scrollviewinformation;

/**
 * Created by cyrilstern1 on 07/09/2017.
 */


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.garminblecompteur.R;

public class FragmentTwo extends Fragment {
    private String title;
    private int image;
    private ImageView mImageView;
    private TextView mHeartCounterView;
    private TextView mSpeedCadenceCounterView;
    private TextView DistanceCounterView;
    private ImageView mImageViewCadence;


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
        mHeartCounterView = (TextView) view.findViewById(R.id.heartView);
        mSpeedCadenceCounterView = (TextView) view.findViewById(R.id.speedcounter);
        DistanceCounterView = (TextView) view.findViewById(R.id.distancecounter);
        mImageView = (ImageView) view.findViewById(R.id.heartimageView);
        mImageViewCadence = (ImageView) view.findViewById(R.id.mImageViewCadence);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * scale hear animation and reverse
         */
        ScaleAnimation mZoomIn = new ScaleAnimation(0.0f , 1.0f,0.0f , 1.0f) ;
        ScaleAnimation mZoomOut = new ScaleAnimation( 0.0f , 0.0f,0.0f , 0.0f) ;
        mZoomIn.setDuration(500);
        mZoomOut.setDuration(1200);
        mZoomOut.setStartOffset(1200+mZoomIn.getStartOffset()+1200);
        mZoomIn.setRepeatCount(Animation.INFINITE);
        mZoomOut.setRepeatCount(Animation.INFINITE);
        mImageView.startAnimation(mZoomIn);
        mImageView.startAnimation(mZoomOut);

        /**
         *  Rotation animation cadence
         */
        RotateAnimation mRotationCadence = new RotateAnimation(0f,359f);
        mRotationCadence.setRepeatCount(Animation.ABSOLUTE);
        mRotationCadence.setDuration(2000);
        mImageViewCadence.startAnimation(mRotationCadence);
    }

    public void setHeartCounter(String heartNbr){
        mHeartCounterView.setText(heartNbr);
    }
    public void setSpeedCounter(String speedNbr){
        mSpeedCadenceCounterView.setText(speedNbr);
    }
    public void setDistanceCounter(String distanceNbr){
        DistanceCounterView.setText(distanceNbr);
    }
    private void loopAnimation(ObjectAnimator objscaleUp, ObjectAnimator objscaleDown){

    }

}