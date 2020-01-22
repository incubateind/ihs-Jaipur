package com.s.hersafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s.hersafety.Model.Location;
import com.s.hersafety.Model.PoliceStation;
import com.s.hersafety.Model.PoliceStationLocation;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);initialiseVariables();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationView.setAnimation("phonesuccess.json");

                for (int i = 0; i<5; i++) {
                    PoliceStation policeStation = new PoliceStation();
                    policeStation.setIncharge("TestIncharge"+i);
                    policeStation.setDistance("0m");
                    policeStation.setArea("AdarshNagar"+i);
                    policeStation.setLocation(new PoliceStationLocation("27.0299139",
                            "75.8936901",
                            "Arya College, Jaipur"));
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("police");

                    reference.child("location" + i).setValue(policeStation);
                }
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });
    }

    private void initialiseVariables() {
        btnLogin = findViewById(R.id.btn_otp);
        animationView = findViewById(R.id.animation_login);
    }


    //final Handler handler = new Handler();
    //                handler.postDelayed(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
    //                    }
    //                }, 2000);
}
