package com.s.policehersafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.s.policehersafety.Model.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationUpdatesPU extends AppCompatActivity {
    private Map map = null;
    private AndroidXMapFragment mapFragment = null;
    List<MapMarker> mapMarkers = new ArrayList<>();
    List<Location> locations = new ArrayList<>();
    Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_updates_pu);
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

                    /*addLocations();
                    addMarkers();*/

                    addMarkers();



                }
            }
        });

        btnBack = findViewById(R.id.locationOfPolice);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationUpdatesPU.this,MainActivity.class));
            }
        });
    }

    private void addMarkers(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("location").child("user1");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("count",String.valueOf(dataSnapshot.getChildrenCount()));
                Common common = new Common();
                for (DataSnapshot abc : dataSnapshot.getChildren()){
                    Location location = abc.getValue(Location.class);

                    common.location.add(location);
                }
                Log.d("Countdklkls", ((String.valueOf(common.location.size()))));



                for (int i = 0; i<common.location.size();i++) {
                    MapMarker defaultMarker = new MapMarker();
                    double lat = Double.parseDouble(common.location.get(i).getLatitude());
                    double lng = Double.parseDouble(common.location.get(i).getLongitude());
                    defaultMarker.setCoordinate(new GeoCoordinate(lat, lng, 0.0));
                    defaultMarker.setTitle("You are here");
                    defaultMarker.setDescription("You are here");
                    mapMarkers.add(defaultMarker);
                    map.addMapObject(mapMarkers.get(i));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
