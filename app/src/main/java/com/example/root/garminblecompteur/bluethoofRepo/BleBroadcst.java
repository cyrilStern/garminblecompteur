package com.example.root.garminblecompteur.bluethoofRepo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

public class BleBroadcst extends BroadcastReceiver implements IBleBroadcst {
    private TextView mContextView;

    public TextView getmContextView() {
        return mContextView;
    }

    public void setmContextView(TextView mContextView) {
        this.mContextView = mContextView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }


    @Override
    public void speedCadenceAction(Intent intent) {

    }

    @Override
    public void heartRateAction(Intent intent) {

    }

    @Override
    public void crankRateAction(Intent intent) {

    }
}
