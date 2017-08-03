package com.example.root.garminblecompteur;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static com.example.root.garminblecompteur.BluetoothServiceGat.ACTION_DATA_AVAILABLE;
import static com.example.root.garminblecompteur.BluetoothServiceGat.ACTION_GATT_CONNECTED;
import static com.example.root.garminblecompteur.BluetoothServiceGat.ACTION_GATT_DISCONNECTED;
import static com.example.root.garminblecompteur.BluetoothServiceGat.ACTION_GATT_SERVICES_DISCOVERED;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 11;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private ListView lv;
    private List <BluetoothDevice> lvDevice;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private final String TAG = LeDeviceListAdapter.class.getName();

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvDevice =  new ArrayList<BluetoothDevice>();
        lv = (ListView) findViewById(R.id.listele);
        mLeDeviceListAdapter = new LeDeviceListAdapter(MainActivity.this, lvDevice);
        lv.setAdapter(mLeDeviceListAdapter);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }else{



        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            Log.i("resuqestcode", String.valueOf(requestCode));
        if (requestCode ==  REQUEST_ENABLE_BT){


        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new Handler(Looper.getMainLooper());
        Log.i("resuqestcode", "insidebluetooth OK");
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            Log.i("resuqestcode", "satrt activityfrresult");
            scanLeDevice(true);
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bthdevice = (BluetoothDevice) parent.getItemAtPosition(position);
                mBluetoothGatt = bthdevice.connectGatt(getApplicationContext(), false, mGattCallback);

            }
        });
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
                        Log.i(TAG, "Connected to GATT server.");
                      //  displayGattServices(mBluetoothLeService.getSupportedGattServices());


                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        mConnectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                    }
                }
                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    Log.w(TAG, "onServicesDiscovered received: " + characteristic.getValue());

                    if (status == BluetoothGatt.GATT_SUCCESS) {
                       // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                }

            };

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mLeDeviceListAdapter.getPosition(device) == -1){
                            Log.i("testarray", String.valueOf(mLeDeviceListAdapter.getPosition(device)));
                                mLeDeviceListAdapter.add(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            };

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//    private void displayGattServices(List<BluetoothGattService> gattServices) {
//        if (gattServices == null) return;
//        String uuid = null;
//        String unknownServiceString = getResources().
//                getString(R.string.unknown_service);
//        String unknownCharaString = getResources().
//                getString(R.string.unknown_characteristic);
//        ArrayList<HashMap<String, String>> gattServiceData =
//                new ArrayList<HashMap<String, String>>();
//        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
//                = new ArrayList<ArrayList<HashMap<String, String>>>();
//        ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
//                new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
//
//        // Loops through available GATT Services.
//        for (BluetoothGattService gattService : gattServices) {
//            HashMap<String, String> currentServiceData =
//                    new HashMap<String, String>();
//            uuid = gattService.getUuid().toString();
//            currentServiceData.put(
//                    LIST_NAME, SampleGattAttributes.
//                            lookup(uuid, unknownServiceString));
//            currentServiceData.put(LIST_UUID, uuid);
//            gattServiceData.add(currentServiceData);
//
//            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
//                    new ArrayList<HashMap<String, String>>();
//            List<BluetoothGattCharacteristic> gattCharacteristics =
//                    gattService.getCharacteristics();
//            ArrayList<BluetoothGattCharacteristic> charas =
//                    new ArrayList<BluetoothGattCharacteristic>();
//            // Loops through available Characteristics.
//            for (BluetoothGattCharacteristic gattCharacteristic :
//                    gattCharacteristics) {
//                charas.add(gattCharacteristic);
//                HashMap<String, String> currentCharaData =
//                        new HashMap<String, String>();
//                uuid = gattCharacteristic.getUuid().toString();
//                currentCharaData.put(
//                        LIST_NAME, SampleGattAttributes.lookup(uuid,
//                                unknownCharaString));
//                currentCharaData.put(LIST_UUID, uuid);
//                gattCharacteristicGroupData.add(currentCharaData);
//            }
//            mGattCharacteristics.add(charas);
//            gattCharacteristicData.add(gattCharacteristicGroupData);
//        }
//
//    }

//    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
//        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                updateConnectionState(R.string.connected);
//                invalidateOptionsMenu();
//            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
//                updateConnectionState(R.string.disconnected);
//                invalidateOptionsMenu();
//                clearUI();
//            } else if (BluetoothLeService.
//                    ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                // Show all the supported services and characteristics on the
//                // user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());
//            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//            }
//        }
//    };


}
