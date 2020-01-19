package com.s.policehersafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s.policehersafety.Model.Location;
import com.s.policehersafety.Model.UserInfo;
import com.s.policehersafety.Model.UserName;
import com.s.policehersafety.ViewHolder.GetUserLocation;
import com.s.policehersafety.ViewHolder.UserVH;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /*RecyclerView recyclerView;
    DatabaseReference category;
    FirebaseRecyclerAdapter<UserName, UserVH> adapter;
    FirebaseRecyclerOptions<UserName> options;
*/

    Button btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSend = findViewById(R.id.btnSet);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LocationUpdatesPU.class));
            }
        });
       /* category = FirebaseDatabase.getInstance().getReference("location");
        recyclerView = findViewById(R.id.recycler_location);

        searchForPS();

        if (adapter!=null)
            adapter.startListening();
        recyclerView.setAdapter(adapter);*/
    }


    /*private void searchForPS(){
        options= new FirebaseRecyclerOptions.Builder<UserName>()
                .setQuery(category, UserName.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FirebaseRecyclerAdapter<UserName, UserVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserVH getUserLocation, int i, @NonNull UserName location) {
                getUserLocation.txtName.setText(location.getName());

            }

            @NonNull
            @Override
            public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_name, parent, false);

                return new UserVH(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private String getAddress(double lati, double longi) {
        String fulladd="";
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
    }*/
}
