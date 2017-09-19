package com.example.root.garminblecompteur;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.garminblecompteur.alertDialogRepo.AlertDialogCompose;
import com.example.root.garminblecompteur.bluethoofRepo.BleBroadcst;
import com.example.root.garminblecompteur.scrollviewinformation.FragmentOne;
import com.example.root.garminblecompteur.scrollviewinformation.FragmentThree;
import com.example.root.garminblecompteur.scrollviewinformation.FragmentTwo;
import com.example.root.garminblecompteur.scrollviewinformation.ScreenSlidePagerAdapter;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends AppCompatActivity implements AlertDialogCompose.NoticeDialogListener{
    private static final int REQUEST_ENABLE_BT = 11;
    private final String TAG = LeDeviceListAdapter.class.getName();
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager locationManager;
    private List<BluetoothDevice> lvDevice;
    private CalculationBikeCommon calculationBikeCommon;
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;



    private ActionBarDrawerToggle mDrawerToggle;
    private File file;
    private ListView listViewGpsFile;
    private Marker positionUser;
    private MapView map;
    private Polyline lineSave;
    private Fragment mCurrentFragment;
    private ViewPager pager;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        calculationBikeCommon = new CalculationBikeCommon();

        /**
         * creation fragmentComputer sliding view
         */
        setContentView(R.layout.activity_main);
        List fragments = new Vector();

        // Ajout des Fragments dans la liste
        fragments.add(Fragment.instantiate(this,FragmentOne.class.getName()));
        fragments.add(Fragment.instantiate(this,FragmentTwo.class.getName()));
        fragments.add(Fragment.instantiate(this,FragmentThree.class.getName()));

        // Création de l'adapter qui s'occupera de l'affichage de la liste de
        // Fragments
        pager = (ViewPager) super.findViewById(R.id.computerpager);
        this.mPagerAdapter = new ScreenSlidePagerAdapter(super.getSupportFragmentManager(), fragments);
        pager.setAdapter(this.mPagerAdapter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /**
         * SideMenuCreation
         */
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("RideSence Live");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Parcours");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        /**
         * case poline exist
         */
      //  if(savedInstanceState != null)  map.getOverlayManager().add((Polyline) savedInstanceState.getSerializable("polineSave"));
//        map.invalidate();

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
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this,"this is emulated device",Toast.LENGTH_SHORT).show();

        }else{
            final ArrayList<FileContainer> arraylistFileContainer = fileService.getListFile("gpstrace");
            ArrayList<String> listNameGpsFile = new ArrayList<>();
            for (FileContainer fileGps: arraylistFileContainer) {
                listNameGpsFile.add(fileGps.getName());
            }
            /** Bring overlay MenuSide to the front. **/
            listViewGpsFile.bringToFront();
            listViewGpsFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String pathFromItem = arraylistFileContainer.get(position).getPath();
                    XmlToGeoJson xmlToGeoJson = XmlToGeoJson.getInstance();
                    mDrawerLayout.closeDrawers();


                    /** Load GpsPoint from reading file.**/
                    try {
                        ArrayList<GeoPoint> waypoints = xmlToGeoJson.decodeXmlToGeoJson(pathFromItem, getApplicationContext());

                        // depra
                        Polyline line = new Polyline(getApplicationContext());
                        line.setTitle("Central Park, NYC");
                        line.setSubDescription(Polyline.class.getCanonicalName());
                        line.setWidth(10);
                        List<GeoPoint> pts = new ArrayList<>();
                        line.setPoints(waypoints);
                        line.setGeodesic(true);
                        //line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, map));
                        lineSave = line;
                        if(mCurrentFragment instanceof FragmentOne){
                            ((FragmentOne) mCurrentFragment).setTrace(line);
                        }

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
            listViewGpsFile.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.linearlisteviewgpx, listNameGpsFile ));
        }

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
       // outState.putSerializable("polineSave", (Serializable) lineSave);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {

                BroadcastReceiver mbluetoothServiceGat = new BleBroadcst() {
                    private void speedCadenceAction(final Intent intent){
                        Log.i(getPackageName(), "speedCadenceAction: ");
                        if(mCurrentFragment instanceof FragmentTwo){
                            Log.i(getPackageName(), "speedCadenceAction: ");

                            ((FragmentTwo) mCurrentFragment).setHeartCounter("heartrate");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bundle b = intent.getExtras();
                                String speed = String.valueOf(calculationBikeCommon.speedcalculation(b.getInt("WheelRevolution"),b.getInt("CrankRevolutions")));
                                if(mCurrentFragment instanceof FragmentTwo){
                                    Log.i(getPackageName(), "speedCadenceAction: " + speed);

                                    ((FragmentTwo) mCurrentFragment).setSpeedCounter(speed);
                                }

                            }
                        });

                    }
                    private void speedHeartAction(final Intent intent){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bundle b = intent.getExtras();
                                if(mCurrentFragment instanceof FragmentTwo){
                                    Log.i(getPackageName(), "heartRate: " + (b.getString("heartRate")));
                                    ((FragmentTwo) mCurrentFragment).setSpeedCounter(b.getString("heartRate"));
                                }
                            }
                        });

                    }
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.i(getPackageName(), "onReceive: broadcastreceiveraction: " + intent.getAction());
                        Bundle b  = intent.getExtras();

                        switch (b.getString("filter")){
                            case BluetoothServiceGat.HEART_RATE:
                                speedHeartAction(intent);
                                break;
                            case BluetoothServiceGat.SPEED_CADENCE:
                                speedCadenceAction(intent);
                                break;
                            default:

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                };

                IntentFilter filter= new IntentFilter(BluetoothServiceGat.ACTION_BLE_SERVICE);
                getApplicationContext().registerReceiver(mbluetoothServiceGat,filter);

                //BluetoothDevice bluetoothDevice = data.getParcelableExtra("DEVICE");
                // Log.i("thisisit",bluetoothDevice.getName());
                //bluetoothDevice.connectGatt(this,false,gattCallback);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        mCurrentFragment = mPagerAdapter.getItem(pager.getCurrentItem());

        /**
         * getTheCurrentFragment
         */

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentFragment = mPagerAdapter.getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        /**
         * dialog box if location is diseable on starting.
         */

        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (off == 0) {
            AlertDialogCompose alertDialgCompose = AlertDialogCompose.newInstance("Votre service de localisation n'est pas allumé, désirez vous le connecter?", "oui","non");
            alertDialgCompose.show(getFragmentManager(),"alertLocation");
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
                if(mCurrentFragment instanceof FragmentOne){
                    ((FragmentOne) mCurrentFragment).setPositionMarker(location);
                }
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

    public void startDicoveredBle (){
        Intent intent = new Intent(this,Listdevices.class);
        startActivityForResult(intent,REQUEST_ENABLE_BT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_first, menu);

        // return true so that the menu pop up is opened
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.bleActivity:
                startDicoveredBle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(onGPS);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
