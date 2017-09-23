package com.example.root.garminblecompteur;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothServiceGat extends Service {
    public final static  String ACTION_BLE_SERVICE =
            "com.example.root.garminblecompteur";
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String HEART_RATE =
            "com.example.bluetooth.le.HEART_RATE";
    public final static String SPEED_CADENCE =
            "com.example.bluetooth.le.SPEED_CADENCE";
    public static final String BROADCAST_WHEEL_DATA = "com.example.root.garminblecompteur.BluetoothServiceGat.BROADCAST_WHEEL_DATA";
    public static final String EXTRA_SPEED = "com.example.root.garminblecompteur.BluetoothServiceGat.EXTRA_SPEED";
    /**
     * Distance in meters
     */
    public static final String EXTRA_DISTANCE = "com.example.root.garminblecompteur.BluetoothServiceGat.EXTRA_DISTANCE";
    /**
     * Total distance in meters
     */
    public static final String EXTRA_TOTAL_DISTANCE = "com.example.root.garminblecompteur.BluetoothServiceGat.EXTRA_TOTAL_DISTANCE";
    public static final String BROADCAST_CRANK_DATA = "com.example.root.garminblecompteur.BluetoothServiceGat.BROADCAST_CRANK_DATA";
    public static final String EXTRA_GEAR_RATIO = "com.example.root.garminblecompteur.BluetoothServiceGat.EXTRA_GEAR_RATIO";
    public static final String EXTRA_CADENCE = "com.example.root.garminblecompteur.BluetoothServiceGat.EXTRA_CADENCE";
    private final static String TAG = BluetoothServiceGat.class.getSimpleName();
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private int mFirstWheelRevolutions = -1;
    private int mLastWheelRevolutions = -1;
    private int mLastWheelEventTime = -1;
    private float mWheelCadence = -1;
    private int mLastCrankRevolutions = -1;
    private int mLastCrankEventTime = -1;
//
//    public final static UUID UUID_HEART_RATE_MEASUREMENT =
//            UUID.fromString("");
    private final BluetoothGattCallback mGattCallback =
        new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                int newState) {
                String intentAction;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    intentAction = ACTION_GATT_CONNECTED;
                    mConnectionState = STATE_CONNECTED;
                    broadcastUpdate(intentAction);
                    Log.i(TAG, "Connected to GATT server.");
                    Log.i(TAG, "Attempting to start service discovery:" +
                            gatt.discoverServices());

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    intentAction = ACTION_GATT_DISCONNECTED;
                    mConnectionState = STATE_DISCONNECTED;
                    Log.i(TAG, "Disconnected from GATT server.");
                    broadcastUpdate(intentAction);
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                Log.i(getPackageName(), "onCharacteristicChanged: ");
                if (String.valueOf(BluetoothResolver.resolveCharacteristicName(String.valueOf(characteristic.getUuid()))).equals("CSC Measurement")) {
                    int offset = 0;
                    offset += 1;
                    //  Log.i("onCharacteristicChange", String.valueOf(BluetoothResolver.resolveCharacteristicName(String.valueOf(characteristic.getUuid()))));
                    int wheelRevolutions = 0;
                    int lastWheelEventTime = 0;
                    if (true) {
                        wheelRevolutions = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, offset);
                        offset += 4;

                        lastWheelEventTime = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset); // 1/1024 s
                        offset += 2;
                    }
                    int crankRevolutions = 0;
                    int lastCrankEventTime = 0;
                    if (true) {
                        crankRevolutions = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                        offset += 2;

                        lastCrankEventTime = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                        offset += 2;

                    }
                    // Log.i(TAG + "onCharacteristicChange",wheelRevolutions + "/"+ lastWheelEventTime + "/" + lastCrankEventTime);
                    final int finalWheelRevolutions = wheelRevolutions;
                    final int finalLastCrankEventTime = lastCrankEventTime;
                    final int finalCrankRevolutions = crankRevolutions;
                    final int finalLastWheelEventTime = lastWheelEventTime;

                    /**
                     * Intent speed/cadence mesuration information
                     */
                    onWheelMeasurementReceived(finalWheelRevolutions, finalLastWheelEventTime);
                    onCrankMeasurementReceived(finalCrankRevolutions, finalLastCrankEventTime);


                }

                /**
                 * Intent heart information
                 */
                if (String.valueOf(BluetoothResolver.resolveCharacteristicName(String.valueOf(characteristic.getUuid()))).equals("Heart Rate Measurement")) {
                    final int heartRate = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
                    final Intent intent = new Intent(ACTION_BLE_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putInt("heartRate",heartRate);
                    bundle.putString("filter",HEART_RATE);
                    intent.putExtras(bundle);
                    sendBroadcast(intent);

                }
            }

            @Override
            // New services discovered
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {

                    List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());

            for (BluetoothGattService service : services) {
                Log.i("onServicesDiscovered1", String.valueOf(BluetoothResolver.resolveServiceName(service.getUuid().toString())));
                if (String.valueOf(BluetoothResolver.resolveServiceName(service.getUuid().toString())).equals("Cycling Speed and Cadence")) {
                    for (BluetoothGattCharacteristic cara : service.getCharacteristics()) {
                        gatt.setCharacteristicNotification(cara, true);
                        //get all cara from device
                        Log.i("onServicesDiscovered2", String.valueOf(BluetoothResolver.resolveCharacteristicName(String.valueOf(cara.getUuid()))));


                        for (BluetoothGattDescriptor descriptor : cara.getDescriptors()) {
                            //find descriptor UUID that matches Client Characteristic Configuration (0x2902)
                            // and then call setValue on that descriptor
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
                if (String.valueOf(BluetoothResolver.resolveServiceName(service.getUuid().toString())).equals("Heart Rate")) {
                    for (BluetoothGattCharacteristic cara : service.getCharacteristics()) {
                        gatt.setCharacteristicNotification(cara, true);
                        Log.i("onServicesDiscovered2", String.valueOf(BluetoothResolver.resolveCharacteristicName(String.valueOf(cara.getUuid()))));
                        for (BluetoothGattDescriptor descriptor : cara.getDescriptors()) {
                            //find descriptor UUID that matches Client Characteristic Configuration (0x2902)
                            // and then call setValue on that descriptor
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
                   // broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            }

            @Override
            // Result of a characteristic read operation
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                   // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                }
            }
        };

    public BluetoothServiceGat() {
        Log.i(getPackageName(), "BluetoothServiceGat: service start");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        BluetoothDevice mbluetoothDevice = intent.getParcelableExtra("DEVICE");
        Log.i(getPackageName(), "onStartCommand: " + mbluetoothDevice.getName());
        mbluetoothDevice.connectGatt(getApplicationContext(), false, mGattCallback);
        // Various callback methods defined by the BLE API.

        return START_STICKY;

    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * calculation speed distance cranck revolution
     *
     * @param wheelRevolutions
     * @param lastWheelEventTime
     */
    public void onWheelMeasurementReceived(final int wheelRevolutions, final int lastWheelEventTime) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int circumference = 2096; // [mm]

        if (mFirstWheelRevolutions < 0)
            mFirstWheelRevolutions = wheelRevolutions;

        if (mLastWheelEventTime == lastWheelEventTime)
            return;

        if (mLastWheelRevolutions >= 0) {
            float timeDifference = 0;
            if (lastWheelEventTime < mLastWheelEventTime)
                timeDifference = (65535 + lastWheelEventTime - mLastWheelEventTime) / 1024.0f; // [s]
            else
                timeDifference = (lastWheelEventTime - mLastWheelEventTime) / 1024.0f; // [s]
            final float distanceDifference = (wheelRevolutions - mLastWheelRevolutions) * circumference / 1000.0f; // [m]
            final float totalDistance = (float) wheelRevolutions * (float) circumference / 1000.0f; // [m]
            final float distance = (float) (wheelRevolutions - mFirstWheelRevolutions) * (float) circumference / 1000.0f; // [m]
            final float speed = (distanceDifference / 1000) / (timeDifference / 3600);
            mWheelCadence = (wheelRevolutions - mLastWheelRevolutions) * 60.0f / timeDifference;

            final Intent broadcast = new Intent(BROADCAST_WHEEL_DATA);
            broadcast.putExtra("filter", BluetoothServiceGat.BROADCAST_WHEEL_DATA);
            broadcast.putExtra(EXTRA_SPEED, speed);
            broadcast.putExtra(EXTRA_DISTANCE, distance);
            broadcast.putExtra(EXTRA_TOTAL_DISTANCE, totalDistance);
            sendBroadcast(broadcast);
        }
        mLastWheelRevolutions = wheelRevolutions;
        mLastWheelEventTime = lastWheelEventTime;
    }

    public void onCrankMeasurementReceived(int crankRevolutions, int lastCrankEventTime) {
        if (mLastCrankEventTime == lastCrankEventTime)
            return;

        if (mLastCrankRevolutions >= 0) {
            float timeDifference = 0;
            if (lastCrankEventTime < mLastCrankEventTime)
                timeDifference = (65535 + lastCrankEventTime - mLastCrankEventTime) / 1024.0f; // [s]
            else
                timeDifference = (lastCrankEventTime - mLastCrankEventTime) / 1024.0f; // [s]

            final float crankCadence = (crankRevolutions - mLastCrankRevolutions) * 60.0f / timeDifference;
            if (crankCadence > 0) {
                final float gearRatio = mWheelCadence / crankCadence;

                final Intent broadcast = new Intent(BROADCAST_CRANK_DATA);
                broadcast.putExtra(EXTRA_GEAR_RATIO, gearRatio);
                broadcast.putExtra(EXTRA_CADENCE, (int) crankCadence);
                sendBroadcast(broadcast);
            }
        }
        mLastCrankRevolutions = crankRevolutions;
        mLastCrankEventTime = lastCrankEventTime;
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);


    }

}
