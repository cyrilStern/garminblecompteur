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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.root.garminblecompteur.scrollviewinformation.FragmentTwo;

import java.util.List;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothServiceGat extends Service {
    private final static String TAG = BluetoothServiceGat.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

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
//
//    public final static UUID UUID_HEART_RATE_MEASUREMENT =
//            UUID.fromString("");

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
        BluetoothDevice mbluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("DEVICE");
        Log.i(getPackageName(), "onStartCommand: " +  mbluetoothDevice.getName());
        mbluetoothDevice.connectGatt(getApplicationContext(),false,mGattCallback);
        // Various callback methods defined by the BLE API.

        return START_STICKY;

    }
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
                    Log.i("onCharacteristicChange", String.valueOf(BluetoothResolver.resolveCharacteristicName(String.valueOf(characteristic.getUuid()))));

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
                    final int finalWheelRevolutions = wheelRevolutions;
                    final int finalCrankRevolutions = crankRevolutions;
                    final int finalLastCrankEventTime = lastCrankEventTime;
                    final int finalLastWheelEventTime = lastWheelEventTime;

                    /**
                     * Intent speed/cadence mesuration information
                     */

                    final Intent intent = new Intent(ACTION_BLE_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("filter",SPEED_CADENCE);
                    bundle.putInt("WheelRevolution",finalWheelRevolutions);
                    bundle.putInt("CrankRevolutions",finalCrankRevolutions);
                    bundle.putInt("LastCrankEventTime",finalLastCrankEventTime);
                    bundle.putInt("LastWheelEventTime",finalLastWheelEventTime);
                    intent.putExtras(bundle);
                    sendBroadcast(intent);

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

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            int flag = characteristic.getProperties();
//            int format = -1;
//            if ((flag & 0x01) != 0) {
//                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "Heart rate format UINT16.");
//            } else {
//                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "Heart rate format UINT8.");
//            }
//            final int heartRate = characteristic.getIntValue(format, 1);
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
//            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
//        } else {
//            // For all other profiles, writes the data formatted in HEX.
//            final byte[] data = characteristic.getValue();
//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//                for(byte byteChar : data)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
//                        stringBuilder.toString());
//            }
       // }
    }

}
