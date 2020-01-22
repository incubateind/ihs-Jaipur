package com.s.hersafety;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s.hersafety.Model.PoliceStation;
import com.s.hersafety.ViewHolder.PoliceStationInfoViewHolder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ListOfPoliceStations extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference category;
    FirebaseRecyclerAdapter<PoliceStation, PoliceStationInfoViewHolder> adapter;
    FirebaseRecyclerOptions<PoliceStation> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_police_stations);
        category = FirebaseDatabase.getInstance().getReference("police");
        recyclerView = findViewById(R.id.recyclerPolice);

        /*adapter = new FirebaseRecyclerAdapter<PoliceStation, PoliceStationInfoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PoliceStationInfoViewHolder policeStationInfoViewHolder, int i, @NonNull PoliceStation policeStation) {
                policeStationInfoViewHolder.txtPhone.setText(policeStation.getPhone());
                policeStationInfoViewHolder.txtDistance.setText(getAddress(
                        Double.parseDouble(policeStation.getLocation().getLatitude()),
                        Double.parseDouble(policeStation.getLocation().getLongitude())));
                policeStationInfoViewHolder.txtArea.setText(policeStation.getArea());
                policeStationInfoViewHolder.txtIncharge.setText(policeStation.getIncharge());
                policeStationInfoViewHolder.btnSOS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO
                    }
                });

                policeStationInfoViewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO
                    }
                });
                policeStationInfoViewHolder.btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO
                    }
                });
            }

            @NonNull
            @Override
            public PoliceStationInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_police, parent, false);

                return new PoliceStationInfoViewHolder(itemView);
            }
        };
       */ LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        if (adapter!=null)
            adapter.startListening();
        recyclerView.setAdapter(adapter);

        searchForPS();
    }






   private void searchForPS(){
       options= new FirebaseRecyclerOptions.Builder<PoliceStation>()
               .setQuery(category,PoliceStation.class).build();
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       adapter = new FirebaseRecyclerAdapter<PoliceStation, PoliceStationInfoViewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull PoliceStationInfoViewHolder policeStationInfoViewHolder, int i, @NonNull PoliceStation policeStation) {
               policeStationInfoViewHolder.txtPhone.setText(policeStation.getPhone());
               double lat = Double.parseDouble(policeStation.getLocation().getLatitude());
               double lng = Double.parseDouble(policeStation.getLocation().getLongitude());
               double distan = getDistance(lat+1,lng+1,lat,lng)/1000;
               Toast.makeText(ListOfPoliceStations.this, ""+distan, Toast.LENGTH_SHORT).show();

               policeStationInfoViewHolder.txtDistance.setText(distan+"");
               policeStationInfoViewHolder.txtArea.setText(policeStation.getArea());
               policeStationInfoViewHolder.txtIncharge.setText(policeStation.getIncharge());
               policeStationInfoViewHolder.btnSOS.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       //TODO
                   }
               });

               policeStationInfoViewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       //TODO
                   }
               });
               policeStationInfoViewHolder.btnCall.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       //TODO
                   }
               });

           }

           @NonNull
           @Override
           public PoliceStationInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_police, parent, false);

               return new PoliceStationInfoViewHolder(itemView);
           }
       };
       LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
               R.anim.layout_fall_down);
       recyclerView.setLayoutAnimation(controller);

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
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(adapter!=null)
        {
            adapter.startListening();
        }
    }

    public static double getDistance(double lat1, double lon1, double lat2, double lon2)
    {
        double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563;
        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
        double cosSqAlpha;
        double sinSigma;
        double cos2SigmaM;
        double cosSigma;
        double sigma;

        double lambda = L, lambdaP, iterLimit = 100;
        do
        {
            double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt(	(cosU2 * sinLambda)
                    * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                    * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            );
            if (sinSigma == 0)
            {
                return 0;
            }

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = 	L + (1 - C) * f * sinAlpha
                    * 	(sigma + C * sinSigma
                    * 	(cos2SigmaM + C * cosSigma
                    * 	(-1 + 2 * cos2SigmaM * cos2SigmaM)
            )
            );

        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
        {
            return 0;
        }

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384
                * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma =
                B * sinSigma
                        * (cos2SigmaM + B / 4
                        * (cosSigma
                        * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                        * (-3 + 4 * sinSigma * sinSigma)
                        * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

        double s = b * A * (sigma - deltaSigma);

        return s;
    }
}
