package com.example.dzhohar.googlemaps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class OblibenaMistaActivity extends AppCompatActivity {
    ListView listView;
    static ArrayList<String> places = new ArrayList<String>();
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorable_place);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.dzhohar.googlemaps", Context.MODE_PRIVATE);

        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();

        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();

        try {

            places = (ArrayList<String>) ListSerializace.deserialize(sharedPreferences.getString("places", ListSerializace.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ListSerializace.deserialize(sharedPreferences.getString("lats", ListSerializace.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ListSerializace.deserialize(sharedPreferences.getString("lons", ListSerializace.serialize(new ArrayList<String>())));


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (places.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0) {
            if (places.size() == latitudes.size() && places.size() == longitudes.size()) {
                for (int i=0; i < latitudes.size(); i++) {
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i))));
                }
            }
        } else {
            places.add("Add a new place...");
            locations.add(new LatLng(0,0));
        }


       listView = findViewById(R.id.listView);


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, places);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), Maps2Activity.class);
                intent.putExtra("placeNumber",i);

                startActivity(intent);
            }
        });


    }

    public void vymazatObsah(View view) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.dzhohar.googlemaps", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        places.clear();
        locations.clear();
        places.add("Add a new place...");arrayAdapter.notifyDataSetChanged();
        locations.add(new LatLng(0,0));
    }
}