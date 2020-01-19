package com.s.hersafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.s.hersafety.Model.Location;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Map map = null;
    private AndroidXMapFragment mapFragment = null;
    List<MapMarker> mapMarkers = new ArrayList<>();
    List<Location> locations = new ArrayList<>();
    Button requestLocation, policeAcitvity, listPoliceStations;
    MyBackgroundService service = null;
    boolean mBound = false;

    private static final long UPDATE_INTERVAL_IN_MIL = 1000*60*5;
    private static final float MINIMUM_DISTANCE = 10;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MIL = 10000/2;
    private static final int NOTI_ID = 1223;
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    double iniLat, iniLon;
    public android.location.Location mLocation;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBackgroundService.LocalBinder binder = (MyBackgroundService.LocalBinder)iBinder;
            service = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
            mBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapFragment = (AndroidXMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);

        assert mapFragment != null;
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    map = mapFragment.getMap();
                    map.setCenter(new GeoCoordinate(27.031928999999998, 75.88823952, 0.0),
                            Map.Animation.BOW);
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);

                    addLocations();
                    addMarkers();

                }
            }
        });

        initVariables();

        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ))
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(final MultiplePermissionsReport report) {

                        requestLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(requestLocation.getText().toString().equals("Start Update"))
                                    service.requestLocationUpdates();
                                else
                                    service.removeLocationUpdates();
                            }
                        });



                        setButtonState(Common.requestingLocationUpdates(MainActivity.this));
                        bindService(new Intent(MainActivity.this, MyBackgroundService.class),
                                mServiceConnection,
                                Context.BIND_AUTO_CREATE);

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

        listPoliceStations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ListOfPoliceStations.class));
            }
        });


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                android.location.Location location = locationResult.getLastLocation();
                iniLat = location.getLatitude();
                iniLon = location.getLongitude();


            }
        };

        createLocationRequest();
        getLastLocation();


    }
    private void initVariables() {
        requestLocation = findViewById(R.id.request_location);
        listPoliceStations = findViewById(R.id.listPoliceStations);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(Common.KEY_REQUESTING_LOCATION_UPDATES))
        {
            setButtonState(sharedPreferences.getBoolean(Common.KEY_REQUESTING_LOCATION_UPDATES,false));
        }
    }

    private void setButtonState(boolean isRequestEnable) {
        if(isRequestEnable)
        {
            requestLocation.setText("Stop Update");
        }
        else {
            requestLocation.setText("Start Update");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocaton(SendLocationToActivity event){
        if(event!=null){
            String data = event.getmLocation().getLatitude() +
                    "/" +
                    event.getmLocation().getLongitude();

            Toast.makeText(service, data, Toast.LENGTH_SHORT).show();
        }
    }


    private void addMarkers(){
        for (int i=0;i<locations.size();i++)
        {

            MapMarker defaultMarker = new MapMarker();

            double lat = Double.parseDouble(locations.get(i).getLatitude());
            double lng = Double.parseDouble(locations.get(i).getLongitude());
            defaultMarker.setCoordinate(new GeoCoordinate(lat, lng, 0.0));
            defaultMarker.setTitle("You are here");
            defaultMarker.setDescription("You are here");
            mapMarkers.add(defaultMarker);
            map.addMapObject(mapMarkers.get(i));
        }
    }

    private void addLocations(){
        for(int i=0;i<5;i++)
        {
            Location location = new Location();
            double lat = 27.031928999999998+0.01*i;
            double lon = 75.88823952;
            location.setLatitude(String.valueOf(lat));
            location.setLongitude(String.valueOf(lon));
            location.setAddress(getAddress(this,
                    lat,lon));
            location.setUserName("user1");
            location.setUserPhone("7300126808");
            locations.add(location);
        }
        addLocationsV();
    }


    private void addLocationsV(){
        for(int i=1;i<6;i++)
        {
            Location location = new Location();
            location.setLatitude(String.valueOf(27.031928999999998));
            location.setLongitude(String.valueOf(75.88823952+0.01*i));
            locations.add(location);
        }
    }

    private String getAddress(Context context, double lati, double longi) {
        String fulladd="";
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lati, longi, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                fulladd = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return fulladd;
    }


    private void getLastLocation() {

        try{
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
                        @Override
                        public void onComplete(@NonNull Task<android.location.Location> task) {
                            if(task.isSuccessful()&&task.getResult()!=null){
                                mLocation = task.getResult();
                            }
                            else
                                Log.d("Error", "Cannot get Location");
                        }
                    });
        }
        catch (SecurityException ex)
        {
            Log.e("Error","Lost Location Permission"+ex.getMessage());
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MIL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MIL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(MINIMUM_DISTANCE);

    }


}