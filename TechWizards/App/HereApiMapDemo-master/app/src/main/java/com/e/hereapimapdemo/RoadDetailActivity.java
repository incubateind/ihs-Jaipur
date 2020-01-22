package com.e.hereapimapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import static android.R.id.text1;

public class RoadDetailActivity extends AppCompatActivity {
    ListView roadDetails;
    String [] roadDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_detail);
        roadDetails = findViewById(R.id.roadDetails);
        ArrayAdapter adapter = new ArrayAdapter(RoadDetailActivity.this, android.R.layout.simple_expandable_list_item_2);
    }
}
