package com.example.root.garminblecompteur.bluethoofRepo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import com.example.root.garminblecompteur.BluetoothServiceGat;

public class  BleBroadcst extends BroadcastReceiver{
    public void setmContextView(TextView mContextView) {
        this.mContextView = mContextView;
    }

    public TextView getmContextView() {
        return mContextView;
    }

    private TextView mContextView;

    @Override
    public void onReceive(Context context, Intent intent) {

    }


}
