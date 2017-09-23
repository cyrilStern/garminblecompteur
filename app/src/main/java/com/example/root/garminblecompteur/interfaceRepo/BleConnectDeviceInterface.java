package com.example.root.garminblecompteur.interfaceRepo;

import android.bluetooth.BluetoothDevice;

/**
 * Created by cyrilstern1 on 22/09/2017.
 */

public interface BleConnectDeviceInterface {
    void listernBleConnected(BluetoothDevice bluetoothDevice);
}
