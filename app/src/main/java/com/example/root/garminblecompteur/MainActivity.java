package com.example.root.garminblecompteur;

import android.Manifest;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.util.List;
import java.util.Vector;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends AppCompatActivity implements AlertDialogCompose.NoticeDialogListener{
    private static final int REQUEST_ENABLE_BT = 11;
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;
    private final String TAG = LeDeviceListAdapter.class.getName();
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager locationManager;
    private List<BluetoothDevice> lvDevice;
    private CalculationBikeCommon calculationBikeCommon;
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

    public ScreenSlidePagerAdapter getmPagerAdapter() {
        return mPagerAdapter;
    }

    public ViewPager getPager() {
        return pager;
    }

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


//            listViewGpsFile.setAdapter(new ArrayAdapter<String>(this,
//                    R.layout.linearlisteviewgpx, listNameGpsFile ));
//

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

                BleBroadcst mbluetoothServiceGat = new BleBroadcst() {

                    @Override
                    public TextView getmContextView() {
                        return super.getmContextView();
                    }

                    @Override
                    public void setmContextView(TextView mContextView) {
                        super.setmContextView(mContextView);
                    }

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        super.onReceive(context, intent);
                        Log.i(getPackageName(), "onReceive: broadcastreceiveraction: " + intent.getIntExtra("WheelRevolution", 0) + intent.getIntExtra("LastWheelEventTime", 0));
                        Bundle b = intent.getExtras();

                        switch (b.getString("filter")) {
                            case BluetoothServiceGat.BROADCAST_WHEEL_DATA:
                                speedCadenceAction(intent);
                                break;
                            case BluetoothServiceGat.BROADCAST_CRANK_DATA:
                                crankRateAction(intent);
                                break;
                            case BluetoothServiceGat.HEART_RATE:
                                heartRateAction(intent);
                            default:

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }

                    @Override
                    public void speedCadenceAction(final Intent intent) {
                        super.speedCadenceAction(intent);
                        mCurrentFragment = mPagerAdapter.getItem(getPager().getCurrentItem());
                        if (mCurrentFragment instanceof FragmentTwo) {
                            // ((FragmentTwo) mCurrentFragment).setHeartCounter("heartrate");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                Bundle b = intent.getExtras();
                                    String speed = String.valueOf(b.getFloat(BluetoothServiceGat.EXTRA_SPEED));
                                    String distance = String.valueOf(b.getFloat(BluetoothServiceGat.EXTRA_DISTANCE));
                                    String totaldistance = String.valueOf(b.getFloat(BluetoothServiceGat.EXTRA_TOTAL_DISTANCE));


                                if(mCurrentFragment instanceof FragmentTwo){
                                    ((FragmentTwo) mCurrentFragment).setSpeedCounter(speed, distance, totaldistance);
                                    //((FragmentTwo) mCurrentFragment).setDistanceCounter(distance);

                                }

                            }
                        });
                        }
                    }

                    @Override
                    public void heartRateAction(final Intent intent) {
                        super.heartRateAction(intent);
                        mCurrentFragment = mPagerAdapter.getItem(getPager().getCurrentItem());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bundle b = intent.getExtras();
                                if(mCurrentFragment instanceof FragmentTwo){
                                    Log.i(getPackageName(), "heartRate: " + (b.getString("heartRate")));
                                    ((FragmentTwo) mCurrentFragment).setHeartCounter(b.getString("heartRate"));
                                }
                            }
                        });
                    }

                    @Override
                    public void crankRateAction(final Intent intent) {
                        super.crankRateAction(intent);
                        mCurrentFragment = mPagerAdapter.getItem(getPager().getCurrentItem());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bundle b = intent.getExtras();
                                if (mCurrentFragment instanceof FragmentTwo) {
                                    Log.i(getPackageName(), "heartRate: " + (b.getString("heartRate")));
                                    ((FragmentTwo) mCurrentFragment).setHeartCounter(b.getString("heartRate"));
                                }
                            }
                        });
                    }
                };

                IntentFilter filter = new IntentFilter(BluetoothServiceGat.BROADCAST_WHEEL_DATA);
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
            /**
             * set title actionBar
             *
             * @param position
             * @param positionOffset
             * @param positionOffsetPixels
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle(ScreenSlidePagerAdapter.FRAGMENT0);
                        break;
                    case 1:
                        getSupportActionBar().setTitle(ScreenSlidePagerAdapter.FRAGMENT1);
                        break;
                    case 2:
                        getSupportActionBar().setTitle(ScreenSlidePagerAdapter.FRAGMENT2);
                        break;
                }
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
