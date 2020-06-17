package com.example.dzhohar.googlemaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void pridatMisto(View view) {
        Intent intent = new Intent(getApplicationContext(),OblibenaMistaActivity.class);
        startActivity(intent);
    }



    public void urcitPolohu(View view) {
        Intent intent = new Intent(getApplicationContext(),MyLocation.class);
        startActivity(intent);
    }

    public void planCesty(View view) {
        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        startActivity(intent);
    }
}

