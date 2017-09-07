package com.example.root.garminblecompteur;

import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by cyrilstern1 on 15/08/2017.
 */

public class CalculationBikeCommon {
    private int mFirstWheelRevolutions = -1;
    private int mLastWheelRevolutions = -1;
    private int mLastWheelEventTime = -1;
    private float mWheelCadence = -1;
    private int mLastCrankRevolutions = -1;
    private int mLastCrankEventTime = -1;
    private int distance = 1;
    private float speedcalculation;
    private static final int circumference  = 2096;

    public CalculationBikeCommon() {

    }

    public int adddistanceTodistance(int adddistance){
        this.distance += distance;
        return distance;
    }

    public void resetcompteur(){
        this.distance = 0;
    }


    public float speedcalculation(int wheelRevolutions, int lastWheelEventTime){
        if (mFirstWheelRevolutions < 0)
            mFirstWheelRevolutions = wheelRevolutions;

        if (mLastWheelEventTime == lastWheelEventTime)
            return 0f;

        if (mLastWheelRevolutions >= 0) {
            float timeDifference = 0;
            if (lastWheelEventTime < mLastWheelEventTime)
                timeDifference = (65535 + lastWheelEventTime - mLastWheelEventTime) / 1024.0f; // [s]
            else
                timeDifference = (lastWheelEventTime - mLastWheelEventTime) / 1024.0f; // [s]
            final float distanceDifference = (wheelRevolutions - mLastWheelRevolutions) * circumference / 1000.0f; // [m]
            final float totalDistance = (float) wheelRevolutions * (float) circumference / 1000.0f; // [m]
            final float distance = (float) (wheelRevolutions - mFirstWheelRevolutions) * (float) circumference / 1000.0f; // [m]
            final float speed = (float) ((distanceDifference * 3.6) / timeDifference);
            speedcalculation = speed;
            mWheelCadence = (wheelRevolutions - mLastWheelRevolutions) * 60.0f / timeDifference;


        }
        mLastWheelRevolutions = wheelRevolutions;
        mLastWheelEventTime = lastWheelEventTime;
        return speedcalculation;
    }

}
