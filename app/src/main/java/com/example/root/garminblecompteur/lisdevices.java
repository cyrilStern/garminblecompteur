package com.example.root.garminblecompteur;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import java.util.List;

public class lisdevices extends AppCompatActivity{

    private static final int REQUEST_ENABLE_BT = 11;
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager locationManager;
    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private ListView lv;
    private TextView textViewCardio,textViewSpeed;
    private TextView textViewCadence;
    private List<BluetoothDevice> lvDevice;
    private int mConnectionState = STATE_DISCONNECTED;
    private final String TAG = LeDeviceListAdapter.class.getName();
    private BluetoothGatt mBluetoothGatt;
    private MapView mapView = null;
    private MapboxMap mapboxmap;
    BluetoothGattCharacteristic characteristic;
    private CameraPosition cameraPosition;
    public Marker marker;
    boolean enabled;
    private Icon icon;
    private Marker maker;
    private MarkerViewOptions markerViewOptions;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lisdevices);
        lvDevice = new ArrayList<BluetoothDevice>();
        lv = (ListView) findViewById(R.id.listeviewbluetoothdevice);
        mLeDeviceListAdapter = new LeDeviceListAdapter(lisdevices.this, lvDevice);
        lv.setAdapter(mLeDeviceListAdapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) parent.getItemAtPosition(position);
                Intent i = new Intent();
                i.putExtra("DEVICE", (Parcelable) bluetoothDevice);
                setResult(RESULT_OK, i);
                finish();

            }
        });
        mHandler = new Handler(Looper.getMainLooper());
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            scanLeDevice(true);
        }
    }
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
                                if (mLeDeviceListAdapter.getPosition(device) == -1) {
                                    mLeDeviceListAdapter.add(device);
                                    mLeDeviceListAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                };

}
