package com.s.hersafety;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyBackgroundService extends Service {


    private static final String CHANNEL_ID = "my_channel";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = "com.s.pendingintent.started_from_notification";

    private final IBinder mBinder = new LocalBinder();


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
    public Location mLocation;

    private String data = "";

    public MyBackgroundService()
    {

    }


    @Override
    public void onCreate() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread("Location");
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,false);
        if(startedFromNotification)
        {
            removeLocationUpdates();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    public void removeLocationUpdates() {
        try{
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            Common.setRequestingLocationUpdates(this,false);
            stopSelf();
        }
        catch (SecurityException ex){
            Common.setRequestingLocationUpdates(this,true);
            Log.e("Error","Lost Location Permission Could not remove location updates"+ex.getMessage());
        }
    }

    private void getLastLocation() {

        try{
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
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

    private void onNewLocation(Location lastLocation) {
        mLocation = lastLocation;
        EventBus.getDefault().postSticky(new SendLocationToActivity(mLocation));


        if(serviceRunningInForeground(this)){
            mNotificationManager.notify(NOTI_ID,getNotification());
            updateLocation();
        }
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, MyBackgroundService.class);

        String text = Common.getLocationText(mLocation);

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION,true);

        PendingIntent servicePendingIntent = PendingIntent.getService(this,0
        ,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent activityPendingIntent = PendingIntent.getActivity(this,0,
                new Intent(this, MainActivity.class),0
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launch_black_24dp,"Launch",activityPendingIntent)
                .addAction(R.drawable.ic_cancel_black_24dp,"Cancel",servicePendingIntent)
                .setContentText(text)
                .setContentTitle(Common.getLocationTitile(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launch_black_24dp)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            builder.setChannelId(CHANNEL_ID);
        }
        return builder.build();

    }

    private boolean serviceRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if(getClass().getName().equals(service.service.getClassName()))
                if(service.foreground)
                    return true;

                return false;
    }

    public void requestLocationUpdates() {

        Common.setRequestingLocationUpdates(this,true);
        startService(new Intent(getApplicationContext(),MyBackgroundService.class));
        try{
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,
                    Looper.myLooper());
        }catch (SecurityException ex){
            Log.e("Error","Lost Location Permission Could not remove location updates"+ex.getMessage());
        }
    }

    class LocalBinder extends Binder {

        MyBackgroundService getService(){return MyBackgroundService.this;}
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {

        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(!mChangingConfiguration && Common.requestingLocationUpdates(this))
            startForeground(NOTI_ID,getNotification());
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }



   private void updateLocation(){
       FirebaseDatabase database = FirebaseDatabase.getInstance();
       DatabaseReference myRef = database.getReference("location");

       //myRef.setValue("Hello, World!");

       com.s.hersafety.Model.Location location = new com.s.hersafety.Model.Location();
       location.setLatitude(String.valueOf(mLocation.getLatitude()));
       location.setLongitude(String.valueOf(mLocation.getLongitude()));
       location.setUserName("User1");
       location.setUserPhone("7300126808");
       location.setAddress(getAddress(getApplicationContext(),mLocation.getLatitude(),
               mLocation.getLongitude()));
       myRef.child("user1").child(String.valueOf(System.currentTimeMillis()))
               .setValue(location)
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           Log.d("Msg","Success");
                       }
                   }
               });
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
}
