package com.example.root.garminblecompteur;

import android.Manifest;
import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

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
import static com.example.root.garminblecompteur.BluetoothServiceGat.EXTRA_DATA;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 11;
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager locationManager;
    private boolean mScanning;
    private Handler mHandler;
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
    private Icon icon;
    private Marker maker;
    private MarkerViewOptions markerViewOptions;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private CalculationBikeCommon calculationBikeCommon;
    private static String REQUESTDEVICEBLE = "deviceWanted";
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), "pk.eyJ1IjoiaG9ybmV0bm9pciIsImEiOiJjajY5dDlzeWkwdHMzMzJscWhsZ3l4dDI5In0.wDgG5dXpVVEUNlkAyISDaQ");
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        textViewCardio = (TextView) findViewById(R.id.textView);
        textViewCadence = (TextView) findViewById(R.id.textView2);
        textViewSpeed = (TextView) findViewById(R.id.textViewspeed);
        // map layout fragment
        markerViewOptions = new MarkerViewOptions();
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        icon = iconFactory.fromResource(R.drawable.mapbox_mylocation_icon_default);
        calculationBikeCommon = new CalculationBikeCommon();
// Add the marker to the map

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxmap = mapboxMap;
                maker = mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-33.85699436, 151.21510684)));
                maker.setIcon(icon);

            }
        });
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        } else {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                BluetoothDevice bluetoothDevice = data.getParcelableExtra("DEVICE");
                Log.i("thisisit",bluetoothDevice.getName());
                bluetoothDevice.connectGatt(this,false,gattCallback);
            }

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (off == 0) {
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        }
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(MainActivity.this,
                        "Provider enabled: " + provider, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(MainActivity.this,
                        "Provider disabled: " + provider, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onLocationChanged(Location location) {
                // Do work with new location. Implementation of this method will be covered later.
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(),location.getLongitude())) // Sets the new camera position
                        .zoom(15) // Sets the zoom to level 10
                        .tilt(20) // Set the camera tilt to 20 degrees
                        .build();
               // mapboxmap.setCameraPosition(cameraPosition);
                mapboxmap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                maker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));

            }
        };

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.i("location", "france");

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);

        }
        long minTime = 5 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
        long minDistance = 10;

    }


    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //super.onCharacteristicChanged(gatt, characteristic);



            if (String.valueOf(BluetoothResolver.resolveCharacteristicName(String.valueOf(characteristic.getUuid()))).equals("CSC Measurement")) {
                //final int cyclingcadence = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewCadence.setText(String.valueOf(finalWheelRevolutions));
                        final String speed = String.valueOf(calculationBikeCommon.speedcalculation(finalWheelRevolutions, finalLastWheelEventTime));
                        textViewSpeed.setText(String.valueOf(speed));
                    }
                });
            }
            if (String.valueOf(BluetoothResolver.resolveCharacteristicName(String.valueOf(characteristic.getUuid()))).equals("Heart Rate Measurement")) {
                final int heartRate = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewCardio.setText(String.valueOf(heartRate));
                    }
                });
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());

            for (BluetoothGattService service : services) {
                Log.i("onServicesDiscovered1", String.valueOf(BluetoothResolver.resolveServiceName(service.getUuid().toString())));
                if (String.valueOf(BluetoothResolver.resolveServiceName(service.getUuid().toString())).equals("Cycling Speed and Cadence")) {
                    for (BluetoothGattCharacteristic cara : service.getCharacteristics()
                            ) {
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
                    for (BluetoothGattCharacteristic cara : service.getCharacteristics()
                            ) {
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
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", String.valueOf(BluetoothResolver.resolveCharacteristicName(String.valueOf(characteristic.getUuid()))));

            //gatt.disconnect();
        }
    };

  public void startDicoveredBle (View view){
      Intent intent = new Intent(this,lisdevices.class);
      startActivityForResult(intent,REQUEST_ENABLE_BT);
  }


    //mpa part


}
