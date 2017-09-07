package com.example.root.garminblecompteur;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 11;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static String REQUESTDEVICEBLE = "deviceWanted";
    private final String TAG = LeDeviceListAdapter.class.getName();
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager locationManager;
    private boolean mScanning;
    private Handler mHandler;
    private TextView textViewCardio,textViewSpeed;
    private TextView textViewCadence;
    private List<BluetoothDevice> lvDevice;
    private int mConnectionState = STATE_DISCONNECTED;
    private BluetoothGatt mBluetoothGatt;
    private CalculationBikeCommon calculationBikeCommon;

    /**
     * CallBack Method Get Service
     * ToDo: Make Class outside Main
     */

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
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {

        }
    };


    private ActionBarDrawerToggle mDrawerToggle;
    private int FIRSTLAUNCH = 0;
    private File file;
    private ListView listViewGpsFile;
    private Marker positionUser;
    private MapView map;
    private Polyline lineSave;

    public MainActivity() {
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        org.osmdroid.config.Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_main);

        /**
         * SideMenuCreation
         */
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        FrameLayout fr = (FrameLayout) findViewById(R.id.content_frame);


        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //    getActionBar().setTitle("");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                getActionBar().setTitle("menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /**
         * Load Map
         * Map created for offline with MOBAC
         */
        String atlasName = "Google Map";
        String atlasExtension = ".png";
        int tileSizePixels = 500;
        float defaultLatitude = 48.858093f;
        float defaultLongitude = 2.294694f;
        int minZoom = 1;
        int maxZoom = 17;
        int defaultZoom = 10;

        /**
         * MapCreationBlock
         */
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(new XYTileSource(atlasName, minZoom, maxZoom, tileSizePixels, atlasExtension, new String[] {}));
        map.setBuiltInZoomControls(true);
        map.setBackgroundColor(Color.BLACK);
        map.getController().setZoom(defaultZoom);
        map.setClickable(true);
        map.getController().setCenter(new GeoPoint((int)(defaultLatitude * 1E6), (int)(defaultLongitude * 1E6)));

        /**
         * case poline exist
         */
        if(savedInstanceState.getSerializable("polineSave") != null)  map.getOverlayManager().add((Polyline) savedInstanceState.getSerializable("polineSave"));
        map.invalidate();

        /**
         * optional, but a good way to prevent loading from the network and test your zip loading.
         *
         */
        map.setUseDataConnection(false);

        IMapController mapController = map.getController();
        mapController.setZoom(14);


        /**
         * Creation of the List of traces present in the SDCARD.
         *
         *
         */
        listViewGpsFile = (ListView) findViewById(R.id.left_drawer);


        /**
         * Parse SDCARD to get gpxTrace, and feed listview side memnu
         */
        FileService fileService = FileService.getInstance();
        final ArrayList<FileContainer> arraylistFileContainer = fileService.getListFile("gpstrace");
        ArrayList<String> listNameGpsFile = new ArrayList<>();
        for (FileContainer fileGps: arraylistFileContainer) {
            listNameGpsFile.add(fileGps.getName());
        }

        listViewGpsFile.setAdapter(new ArrayAdapter<String>(this,
                R.layout.linearlisteviewgpx, listNameGpsFile ));

        /** Bring overlay MenuSide to the front. **/
        listViewGpsFile.bringToFront();


        listViewGpsFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pathFromItem = arraylistFileContainer.get(position).getPath();
                XmlToGeoJson xmlToGeoJson = XmlToGeoJson.getInstance();
                mDrawerLayout.closeDrawers();

                /**clear Map before add.**/
                map.getOverlays().clear();

                /** Load GpsPoint from reading file.**/
                try {
                    ArrayList<GeoPoint> waypoints = xmlToGeoJson.decodeXmlToGeoJson(pathFromItem, getApplicationContext());
                    Polyline line = new Polyline(getApplicationContext());
                    line.setTitle("Central Park, NYC");
                    line.setSubDescription(Polyline.class.getCanonicalName());
                    line.setWidth(20f);
                    List<GeoPoint> pts = new ArrayList<>();
                    line.setPoints(waypoints);
                    line.setGeodesic(true);
                    line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, map));
                    lineSave = line;
                    map.getOverlayManager().add(line);
                    map.invalidate();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        positionUser = new Marker(map,getApplicationContext());


        // Set the drawer toggle as the DrawerListener
        getSupportActionBar().hide();
        textViewCardio = (TextView) findViewById(R.id.textView);
        textViewCadence = (TextView) findViewById(R.id.textView2);
        textViewSpeed = (TextView) findViewById(R.id.textViewspeed);


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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lineSave = (Polyline) savedInstanceState.get("polineSave");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("polineSave", (Serializable) lineSave);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                BluetoothDevice bluetoothDevice = data.getParcelableExtra("DEVICE");
                // Log.i("thisisit",bluetoothDevice.getName());
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
                positionUser.setPosition(new GeoPoint(location.getLatitude(),location.getLongitude()));
                positionUser.setPanToView(true);
                map.getOverlays().add(positionUser);
                map.invalidate();
                // Do work with new location. Implementation of this method will be covered later.
//                cameraPosition = new CameraPosition.Builder()
//                        .target(new LatLng(location.getLatitude(),location.getLongitude())) // Sets the new camera position
//                        .zoom(15) // Sets the zoom to level 10
//                        .tilt(20) // Set the camera tilt to 20 degrees
//                        .build();
//                if( FIRSTLAUNCH != 1){
//                    mapboxmap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                    FIRSTLAUNCH = 1;
//                }
//                maker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));

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

    }

    public void startDicoveredBle (View view){
        Intent intent = new Intent(this,Listdevices.class);
        startActivityForResult(intent,REQUEST_ENABLE_BT);
    }

}
