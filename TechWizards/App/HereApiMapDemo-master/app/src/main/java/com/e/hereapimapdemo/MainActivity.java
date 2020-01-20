package com.e.hereapimapdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapviewlite.Camera;
import com.here.sdk.mapviewlite.LayerState;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapLayer;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapOverlay;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapSceneConfig;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.traffic.Incident;
import com.here.sdk.traffic.IncidentCategory;
import com.here.sdk.traffic.IncidentImpact;
import com.here.sdk.traffic.IncidentQueryError;
import com.here.sdk.traffic.IncidentQueryOptions;
import com.here.sdk.traffic.QueryForIncidentsCallback;
import com.here.sdk.traffic.TrafficEngine;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity  {
    enum STATE{
        UPIMG,DOWNIMG
    }
    private STATE state;
    private MapViewLite mapView;
    private Map map = null;
    TextView view_temp,latandlang;
    TextView view_desc;
    ImageView view_weather;
    ImageView launchWeather, changeActivity;
    EditText search;
    Button search_floating;
    CardView cardView;
    MapStyle style;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardView = findViewById(R.id.cardview);
        changeActivity = findViewById(R.id.changeActivity);
        launchWeather = findViewById(R.id.launchWeather);
        view_temp=findViewById(R.id.temp);
        view_temp.setText("");
        view_desc=findViewById(R.id.desc);
        view_desc.setText("");
        latandlang = findViewById(R.id.latandlang);
        launchWeather.animate().translationYBy(400f);

        view_weather=findViewById(R.id.wheather_image);
        search=findViewById(R.id.search_edit);
        search_floating=findViewById(R.id.floating_search);

        state = STATE.UPIMG;
        cardView.animate().translationYBy(1000f);
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        search_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide Keyboard
                if (search.getText().toString().matches("")){
                    Toast.makeText(MainActivity.this,"Please enter a city name",Toast.LENGTH_SHORT).show();
                } else {
                    String address = search.getText().toString();
                    InputMethodManager imm=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getRootView().getWindowToken(),0);
                    api_key(String.valueOf(search.getText()));
                    GeoLocation geoLocation = new GeoLocation();
                    geoLocation.getAddress(address, getApplicationContext(), new GeoHandler());
                }

            }
        });
        launchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE.UPIMG) {
                    launchWeather.setImageDrawable(getResources().getDrawable(R.drawable.downword));
                    launchWeather.animate().translationYBy(-400f).setDuration(500);
                    cardView.animate().translationYBy(-1000f).setDuration(500);
                    state = STATE.DOWNIMG;
                } else if (state == STATE.DOWNIMG){
                    launchWeather.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                    launchWeather.animate().translationYBy(400f).setDuration(500);
                    cardView.animate().translationYBy(1000f).setDuration(500);
                    state = STATE.UPIMG;
                }
            }
        });

        style = MapStyle.NORMAL_DAY;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(style==MapStyle.NORMAL_DAY)
                {
                    style=MapStyle.SATELLITE;
                    fab.setImageResource(R.drawable.normal_view);
                    loadMapScene(style);
                }
                else
                {
                    style=MapStyle.NORMAL_DAY;
                    fab.setImageResource(R.drawable.satellite_view);
                    loadMapScene(style);
                }
            }
        });



        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                loadMapScene(style);
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.e(TAG, "onError: DexterPermissionError occured: "+error.toString() );
                    }
                }).onSameThread()
                .check();

        changeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoadDetailActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadMapScene(MapStyle map_style)
    {
        GeoCoordinates geoCoordinates = null;
        mapView.getMapScene().loadScene(map_style, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
             try {

                 if(errorCode == null) {

//                     trafficExample = new TrafficExample(MainActivity.this, mapView);

                     mapView.getCamera().setTarget(new GeoCoordinates(52.530932, 13.5));
                     mapView.getCamera().setZoomLevel(14);

                     MapImage mapImage = MapImageFactory.fromResource(getResources(), R.drawable.mark);



                     MapMarker mapMarker1 = new MapMarker(new GeoCoordinates(52.530932, 13));
                     MapMarker mapMarker2 = new MapMarker(new GeoCoordinates(52.530932, 13.5));
                     MapMarker mapMarker3 = new MapMarker(new GeoCoordinates(52.530932, 14));

                     mapMarker1.addImage(mapImage, new MapMarkerImageStyle());
                     mapMarker2.addImage(mapImage, new MapMarkerImageStyle());
                     mapMarker3.addImage(mapImage, new MapMarkerImageStyle());




                     mapView.getMapScene().addMapMarker(mapMarker1);
                     mapView.getMapScene().addMapMarker(mapMarker2);
                     mapView.getMapScene().addMapMarker(mapMarker3);

                    Toast.makeText(MainActivity.this,"Done",Toast.LENGTH_SHORT).show();


                 } else {
                     Log.d(TAG, "onLoadScene failed: "+errorCode.toString());
                 }
             } catch (Exception e){
                 e.printStackTrace();
             }
            }
        });
    }



    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    private void api_key(final String City) {
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q="+City+"&appid=c2be2fe3004a8133480e3b5d17ea6345&units=metric")
                .get()
                .build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Response response= client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseData= response.body().string();
                    try {
                        JSONObject json=new JSONObject(responseData);
                        JSONArray array=json.getJSONArray("weather");
                        JSONObject object=array.getJSONObject(0);

                        String description=object.getString("description");
                        String icons = object.getString("icon");

                        JSONObject temp1= json.getJSONObject("main");
                        Double Temperature=temp1.getDouble("temp");


                        String temps=Math.round(Temperature)+" °C";
                        setText(view_temp,temps);
                        setText(view_desc,description);
                        setImage(view_weather,icons);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }


    }
    private void setText(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
    private void setImage(final ImageView imageView, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (value){
                    case "01d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d01d));
                        break;
                    case "01n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d01d));
                        break;
                    case "02d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d02d));
                        break;
                    case "02n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d02d));
                        break;
                    case "03d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d03d));
                        break;
                    case "03n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d03d));
                        break;
                    case "04d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d04d));
                        break;
                    case "04n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d04d));
                        break;
                    case "09d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d09d));
                        break;
                    case "09n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d09d));
                        break;
                    case "10d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d10d));
                        break;
                    case "10n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d10d));
                        break;
                    case "11d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d11d));
                        break;
                    case "11n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d11d));
                        break;
                    case "13d": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d13d));
                        break;
                    case "13n": imageView.setImageDrawable(getResources().getDrawable(R.drawable.d13d));
                        break;
                    default:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.wheather));

                }
            }
        });
    }

    public class GeoHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            String address;
            switch (msg.what){
                case 1:
                    Bundle bundle = msg.getData();
                    address = bundle.getString("address");
                    break;
                    default:
                        address = null;
            }
            latandlang.setText(address);
        }
    }
}
